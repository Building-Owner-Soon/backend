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

    @Scheduled(cron = "0 30 0 * * *") // 매일 새벽 0시 30분에 실행
    fun updateRepaymentStatuses() {
        runBlocking {
            try {
                logger.info("Starting repayment schedule status update batch job")

                val today = LocalDate.now()
                val twoDaysAgo = today.minusDays(2)

                // 1. 연체 상태 업데이트 (scheduled_date < today)
                val overdueCount =
                    repaymentScheduleRepository.updateOverdueStatuses(
                        today,
                        RepaymentStatus.OVERDUE,
                        RepaymentStatus.SCHEDULED,
                        RepaymentStatus.IN_PROGRESS,
                    )
                logger.info("Updated {} schedules to OVERDUE status", overdueCount)

                // 2. 진행중 상태 업데이트 (today-2 <= scheduled_date <= today)
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

    @Scheduled(cron = "0 0 1 * * MON") // 매주 월요일 새벽 1시에 실행
    fun logRepaymentStatusSummary() {
        runBlocking {
            try {
                logger.info("Starting weekly repayment schedule summary")

                val schedulesToUpdate =
                    repaymentScheduleRepository.findSchedulesToUpdate(
                        RepaymentStatus.SCHEDULED,
                        RepaymentStatus.IN_PROGRESS,
                    )
                val statusCount = schedulesToUpdate.groupingBy { it.status }.eachCount()

                logger.info("Weekly repayment schedule summary: {}", statusCount)
            } catch (e: RuntimeException) {
                logger.error("Error occurred during weekly repayment schedule summary", e)
            }
        }
    }
}
