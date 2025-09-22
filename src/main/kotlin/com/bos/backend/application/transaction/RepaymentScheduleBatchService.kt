package com.bos.backend.application.transaction

import com.bos.backend.domain.transaction.enum.RepaymentStatus
import com.bos.backend.domain.transaction.repository.RepaymentScheduleRepository
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class RepaymentScheduleBatchService(
    private val repaymentScheduleRepository: RepaymentScheduleRepository,
) {
    private val logger = LoggerFactory.getLogger(RepaymentScheduleBatchService::class.java)

    @Scheduled(cron = "0 30 0 * * *")
    fun updateRepaymentStatuses() {
        runBlocking {
            try {
                logger.info("Starting repayment schedule status update batch job")

                val today = LocalDate.now()
                val twoDaysAgo = today.minusDays(2)

                val overdueCount =
                    repaymentScheduleRepository.updateOverdueStatuses(
                        twoDaysAgo,
                        RepaymentStatus.OVERDUE,
                        RepaymentStatus.SCHEDULED,
                        RepaymentStatus.IN_PROGRESS,
                    )
                logger.info("Updated {} schedules to OVERDUE status", overdueCount)

                val inProgressCount =
                    repaymentScheduleRepository.updateInProgressStatuses(
                        twoDaysAgo,
                        today,
                        RepaymentStatus.IN_PROGRESS,
                        RepaymentStatus.SCHEDULED,
                    )
                logger.info("Updated {} schedules to IN_PROGRESS status", inProgressCount)

                logger.info(
                    "Completed repayment schedule status update batch job. " +
                        "OVERDUE: {}, IN_PROGRESS: {}",
                    overdueCount,
                    inProgressCount,
                )
            } catch (e: RuntimeException) {
                logger.error("Error occurred during repayment schedule status update batch job", e)
            }
        }
    }
}
