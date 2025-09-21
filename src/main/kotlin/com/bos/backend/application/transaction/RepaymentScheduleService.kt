package com.bos.backend.application.transaction

import com.bos.backend.application.CommonErrorCode
import com.bos.backend.application.CustomException
import com.bos.backend.domain.transaction.entity.RepaymentSchedule
import com.bos.backend.domain.transaction.entity.Transaction
import com.bos.backend.domain.transaction.enum.RepaymentStatus
import com.bos.backend.domain.transaction.enum.RepaymentType
import com.bos.backend.domain.transaction.repository.RepaymentScheduleRepository
import com.bos.backend.domain.transaction.repository.TransactionRepository
import com.bos.backend.presentation.transaction.dto.CreateRepaymentRequestDTO
import com.bos.backend.presentation.transaction.dto.RepaymentManagementResponseDTO
import com.bos.backend.presentation.transaction.dto.RepaymentScheduleItemDTO
import org.springframework.stereotype.Service
import java.math.BigDecimal
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

        val repaymentRecords = repaymentScheduleRepository.findByTransactionIdIn(transactionIds)
        val today = LocalDate.now()
        val repaymentItems = mutableListOf<RepaymentScheduleItemDTO>()

        for (transaction in userTransactions) {
            val repaymentRecords = repaymentRecords.filter { it.transactionId == transaction.id }
            val items = generateRepaymentItems(transaction, repaymentRecords, today)
            repaymentItems.addAll(items)
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

    private fun generateRepaymentItems(
        transaction: Transaction,
        repaymentRecords: List<RepaymentSchedule>,
        today: LocalDate,
    ): List<RepaymentScheduleItemDTO> {
        val items = mutableListOf<RepaymentScheduleItemDTO>()

        repaymentRecords.forEach { record ->
            items.add(
                RepaymentScheduleItemDTO(
                    status = RepaymentStatus.COMPLETED,
                    displayDate = record.repaymentDate,
                    displayAmount = record.repaymentAmount,
                ),
            )
        }

        val totalRepaidAmount = repaymentRecords.sumOf { it.repaymentAmount }
        val remainingAmount = transaction.totalAmount - transaction.completedAmount - totalRepaidAmount

        if (remainingAmount > BigDecimal.ZERO) {
            val scheduledItems = generateScheduledItems(transaction, remainingAmount, today)
            items.addAll(scheduledItems)
        }

        return items
    }

    private fun generateScheduledItems(
        transaction: Transaction,
        remainingAmount: BigDecimal,
        today: LocalDate,
    ): List<RepaymentScheduleItemDTO> =
        when (transaction.repaymentType) {
            RepaymentType.BY_DATE -> {
                val targetDate = transaction.targetDate ?: return emptyList()
                val status = getScheduledStatus(targetDate, today)
                listOf(
                    RepaymentScheduleItemDTO(
                        status = status,
                        displayDate = targetDate,
                        displayAmount = remainingAmount,
                    ),
                )
            }
            RepaymentType.BY_MONTHLY -> {
                generateMonthlyScheduledItems(transaction, remainingAmount, today)
            }
            RepaymentType.FLEXIBLE -> {
                val targetDate = transaction.targetDate ?: return emptyList()
                val status = getScheduledStatus(targetDate, today)
                listOf(
                    RepaymentScheduleItemDTO(
                        status = status,
                        displayDate = targetDate,
                        displayAmount = remainingAmount,
                    ),
                )
            }
        }

    private fun generateMonthlyScheduledItems(
        transaction: Transaction,
        remainingAmount: BigDecimal,
        today: LocalDate,
    ): List<RepaymentScheduleItemDTO> {
        val monthlyAmount = transaction.monthlyAmount
        val paymentDay = transaction.paymentDay

        if (monthlyAmount == null || paymentDay == null) {
            return emptyList()
        }

        val items = mutableListOf<RepaymentScheduleItemDTO>()
        var currentAmount = remainingAmount
        var currentDate = getNextPaymentDate(transaction.transactionDate, paymentDay)

        while (currentAmount > BigDecimal.ZERO) {
            val paymentAmount = if (currentAmount >= monthlyAmount) monthlyAmount else currentAmount
            val status = getScheduledStatus(currentDate, today)

            items.add(
                RepaymentScheduleItemDTO(
                    status = status,
                    displayDate = currentDate,
                    displayAmount = paymentAmount,
                ),
            )

            currentAmount -= paymentAmount
            currentDate = currentDate.plusMonths(1).withDayOfMonth(paymentDay)
        }

        return items
    }

    private fun getScheduledStatus(
        scheduledDate: LocalDate,
        today: LocalDate,
    ): RepaymentStatus =
        when {
            scheduledDate.isBefore(today) -> RepaymentStatus.OVERDUE
            scheduledDate >= today.minusDays(2) && scheduledDate <= today ->
                RepaymentStatus.IN_PROGRESS
            else -> RepaymentStatus.SCHEDULED
        }

    private fun getNextPaymentDate(
        transactionDate: LocalDate,
        paymentDay: Int,
    ): LocalDate {
        val currentMonth = transactionDate.withDayOfMonth(paymentDay)
        return if (currentMonth.isAfter(transactionDate)) {
            currentMonth
        } else {
            currentMonth.plusMonths(1)
        }
    }

    suspend fun addRepayment(
        userId: Long,
        transactionId: Long,
        createRepaymentRequestDTO: CreateRepaymentRequestDTO,
    ): RepaymentScheduleItemDTO {
        val transaction =
            transactionRepository.findById(transactionId)
                ?: throw CustomException(CommonErrorCode.RESOURCE_NOT_FOUND)

        if (transaction.userId != userId) {
            throw CustomException(CommonErrorCode.RESOURCE_NOT_FOUND)
        }

        val repaymentSchedule =
            RepaymentSchedule(
                transactionId = transactionId,
                repaymentDate = createRepaymentRequestDTO.repaymentDate,
                repaymentAmount = createRepaymentRequestDTO.repaymentAmount,
            )

        val savedRepayment = repaymentScheduleRepository.save(repaymentSchedule)

        return RepaymentScheduleItemDTO(
            status = RepaymentStatus.COMPLETED,
            displayDate = savedRepayment.repaymentDate,
            displayAmount = savedRepayment.repaymentAmount,
        )
    }
}
