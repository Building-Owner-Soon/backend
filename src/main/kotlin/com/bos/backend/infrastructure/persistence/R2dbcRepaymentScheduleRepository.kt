package com.bos.backend.infrastructure.persistence

import com.bos.backend.domain.transaction.entity.RepaymentSchedule
import com.bos.backend.domain.transaction.enum.RepaymentStatus
import com.bos.backend.domain.transaction.repository.RepaymentScheduleRepository
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface R2dbcRepaymentScheduleRepository :
    RepaymentScheduleRepository,
    CoroutineCrudRepository<RepaymentSchedule, Long> {
    override suspend fun findByTransactionId(transactionId: Long): List<RepaymentSchedule>

    @Query(
        """
        UPDATE repayment_schedules
        SET status = :overdueStatus, updated_at = CURRENT_TIMESTAMP
        WHERE scheduled_date < :today
        AND status IN (:scheduledStatus, :inProgressStatus)
        """,
    )
    override suspend fun updateOverdueStatuses(
        today: LocalDate,
        overdueStatus: RepaymentStatus,
        scheduledStatus: RepaymentStatus,
        inProgressStatus: RepaymentStatus,
    ): Int

    @Query(
        """
        UPDATE repayment_schedules
        SET status = :inProgressStatus, updated_at = CURRENT_TIMESTAMP
        WHERE scheduled_date >= :startDate
        AND scheduled_date <= :endDate
        AND status = :scheduledStatus
        """,
    )
    override suspend fun updateInProgressStatuses(
        startDate: LocalDate,
        endDate: LocalDate,
        inProgressStatus: RepaymentStatus,
        scheduledStatus: RepaymentStatus,
    ): Int

    @Query(
        """
        SELECT * FROM repayment_schedules
        WHERE status IN (:scheduledStatus, :inProgressStatus)
        ORDER BY scheduled_date
        """,
    )
    override suspend fun findSchedulesToUpdate(
        scheduledStatus: RepaymentStatus,
        inProgressStatus: RepaymentStatus,
    ): List<RepaymentSchedule>
}
