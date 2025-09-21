package com.bos.backend.application.transaction

import com.bos.backend.domain.transaction.entity.RepaymentSchedule
import com.bos.backend.domain.transaction.enum.RepaymentStatus
import com.bos.backend.domain.transaction.repository.RepaymentScheduleRepository
import com.bos.backend.domain.transaction.repository.TransactionRepository
import com.bos.backend.presentation.transaction.dto.RepaymentManagementResponseDTO
import com.bos.backend.presentation.transaction.dto.RepaymentScheduleItemDTO
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class RepaymentScheduleService(
    private val repaymentScheduleRepository: RepaymentScheduleRepository,
    private val transactionRepository: TransactionRepository,
) {
    suspend fun getRepaymentManagement(userId: Long): RepaymentManagementResponseDTO {
        val userTransactions = transactionRepository.findByUserId(userId)
        val transactionIds = userTransactions.map { it.id!! }

        if (transactionIds.isEmpty()) {
            return RepaymentManagementResponseDTO(
                overdueRepayments = emptyList(),
                regularRepayments = emptyList(),
            )
        }

        val allRepaymentSchedules = repaymentScheduleRepository.findByTransactionIdIn(transactionIds)

        val today = LocalDate.now()
        val repaymentItems =
            allRepaymentSchedules.map { schedule ->
                toRepaymentScheduleItemDTO(schedule, today)
            }

        val overdueRepayments =
            repaymentItems
                .filter { it.status == RepaymentStatus.OVERDUE }
                .sortedByDescending { it.displayDate }

        val regularRepayments =
            repaymentItems
                .filter { it.status != RepaymentStatus.OVERDUE }
                .sortedByDescending { it.displayDate }

        return RepaymentManagementResponseDTO(
            overdueRepayments = overdueRepayments,
            regularRepayments = regularRepayments,
        )
    }

    private fun getDisplayStatus(
        schedule: RepaymentSchedule,
        today: LocalDate,
    ): RepaymentStatus =
        when {
            schedule.actualDate != null -> RepaymentStatus.COMPLETED
            schedule.scheduledDate.isBefore(today) -> RepaymentStatus.OVERDUE
            schedule.scheduledDate >= today.minusDays(2) && schedule.scheduledDate <= today ->
                RepaymentStatus.IN_PROGRESS
            else -> RepaymentStatus.SCHEDULED
        }

    private fun toRepaymentScheduleItemDTO(
        schedule: RepaymentSchedule,
        today: LocalDate,
    ): RepaymentScheduleItemDTO {
        val displayStatus = getDisplayStatus(schedule, today)
        val (displayDate, displayAmount) =
            when (displayStatus) {
                RepaymentStatus.COMPLETED -> {
                    Pair(schedule.actualDate!!, schedule.actualAmount!!)
                }
                else -> {
                    Pair(schedule.scheduledDate, schedule.scheduledAmount)
                }
            }

        return RepaymentScheduleItemDTO(
            id = schedule.id!!,
            transactionId = schedule.transactionId,
            status = displayStatus,
            displayDate = displayDate,
            displayAmount = displayAmount,
        )
    }
}
