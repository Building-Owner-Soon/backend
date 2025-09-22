package com.bos.backend.application.transaction

import com.bos.backend.application.CommonErrorCode
import com.bos.backend.application.CustomException
import com.bos.backend.domain.transaction.entity.RepaymentSchedule
import com.bos.backend.domain.transaction.entity.Transaction
import com.bos.backend.domain.transaction.enum.RepaymentType
import com.bos.backend.domain.transaction.repository.RepaymentScheduleRepository
import com.bos.backend.domain.transaction.repository.TransactionRepository
import com.bos.backend.presentation.transaction.dto.CreateTransactionRequestDTO
import com.bos.backend.presentation.transaction.dto.TransactionResponseDTO
import com.bos.backend.presentation.transaction.dto.UpdateTransactionRequestDTO
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val repaymentScheduleRepository: RepaymentScheduleRepository,
    private val transactionalOperator: TransactionalOperator,
) {
    suspend fun createTransaction(
        userId: Long,
        createTransactionRequestDTO: CreateTransactionRequestDTO,
    ): TransactionResponseDTO =
        transactionalOperator.executeAndAwait {
            val transaction =
                Transaction(
                    userId = userId,
                    transactionType = createTransactionRequestDTO.transactionType,
                    counterpartName = createTransactionRequestDTO.counterpartName,
                    counterpartCharacter = createTransactionRequestDTO.counterpartCharacter,
                    relationship = createTransactionRequestDTO.relationship,
                    customRelationship = createTransactionRequestDTO.customRelationship,
                    transactionDate = createTransactionRequestDTO.transactionDate,
                    totalAmount = createTransactionRequestDTO.totalAmount,
                    completedAmount = createTransactionRequestDTO.completedAmount ?: BigDecimal.ZERO,
                    memo = createTransactionRequestDTO.memo,
                    repaymentType = createTransactionRequestDTO.repaymentType,
                    targetDate = createTransactionRequestDTO.targetDate,
                    monthlyAmount = createTransactionRequestDTO.monthlyAmount,
                    paymentDay = createTransactionRequestDTO.paymentDay,
                    hasTargetDate = createTransactionRequestDTO.hasTargetDate,
                )

            val savedTransaction = transactionRepository.save(transaction)
            generateRepaymentSchedules(savedTransaction)
            toTransactionResponseDTO(savedTransaction)
        }

    suspend fun getTransactionDetail(
        userId: Long,
        transactionId: Long,
    ): TransactionResponseDTO {
        val transaction =
            transactionRepository.findById(transactionId)
                ?: throw CustomException(CommonErrorCode.RESOURCE_NOT_FOUND)

        if (transaction.userId != userId) {
            throw CustomException(CommonErrorCode.RESOURCE_NOT_FOUND)
        }

        return toTransactionResponseDTO(transaction)
    }

    suspend fun deleteTransaction(
        userId: Long,
        transactionId: Long,
    ): Unit =
        transactionalOperator.executeAndAwait {
            val transaction =
                transactionRepository.findById(transactionId)
                    ?: throw CustomException(CommonErrorCode.RESOURCE_NOT_FOUND)

            if (transaction.userId != userId) {
                throw CustomException(CommonErrorCode.RESOURCE_NOT_FOUND)
            }

            transactionRepository.deleteById(transactionId)
        }

    suspend fun updateTransaction(
        userId: Long,
        transactionId: Long,
        updateTransactionRequestDTO: UpdateTransactionRequestDTO,
    ): TransactionResponseDTO =
        transactionalOperator.executeAndAwait {
            val existingTransaction =
                transactionRepository.findById(transactionId)
                    ?: throw CustomException(CommonErrorCode.RESOURCE_NOT_FOUND)

            if (existingTransaction.userId != userId) {
                throw CustomException(CommonErrorCode.RESOURCE_NOT_FOUND)
            }

            val updatedTransaction =
                existingTransaction.copy(
                    counterpartName = updateTransactionRequestDTO.counterpartName,
                    counterpartCharacter = updateTransactionRequestDTO.counterpartCharacter,
                    relationship = updateTransactionRequestDTO.relationship,
                    customRelationship = updateTransactionRequestDTO.customRelationship,
                    memo = updateTransactionRequestDTO.memo,
                )

            val savedTransaction = transactionRepository.save(updatedTransaction)
            toTransactionResponseDTO(savedTransaction)
        }

    private suspend fun generateRepaymentSchedules(transaction: Transaction) {
        if (transaction.repaymentType == RepaymentType.FLEXIBLE) {
            return
        }

        val schedules =
            when (transaction.repaymentType) {
                RepaymentType.DIVIDED_BY_PERIOD -> generateDividedByPeriodSchedules(transaction)
                RepaymentType.FIXED_MONTHLY -> generateFixedMonthlySchedules(transaction)
                RepaymentType.FLEXIBLE -> emptyList()
            }

        for (schedule in schedules) {
            repaymentScheduleRepository.save(schedule)
        }
    }

    private fun generateDividedByPeriodSchedules(transaction: Transaction): List<RepaymentSchedule> {
        val targetDate = transaction.targetDate ?: return emptyList()
        val paymentDay = transaction.paymentDay ?: 1
        val totalAmount = transaction.totalAmount

        val schedules = mutableListOf<RepaymentSchedule>()
        var currentDate = transaction.createdAt.atZone(java.time.ZoneId.systemDefault()).toLocalDate()

        if (currentDate.dayOfMonth > paymentDay) {
            currentDate = currentDate.plusMonths(1).withDayOfMonth(paymentDay)
        } else {
            currentDate = currentDate.withDayOfMonth(paymentDay)
        }

        val monthsList = mutableListOf<LocalDate>()
        while (currentDate.isBefore(targetDate) || currentDate.isEqual(targetDate)) {
            monthsList.add(currentDate)
            currentDate = currentDate.plusMonths(1)
        }

        if (monthsList.isNotEmpty()) {
            val amountPerPeriod = totalAmount.divide(BigDecimal(monthsList.size), 2, RoundingMode.HALF_UP)

            monthsList.forEachIndexed { index, paymentDate ->
                val amount =
                    if (index == monthsList.size - 1) {
                        totalAmount - amountPerPeriod.multiply(BigDecimal(monthsList.size - 1))
                    } else {
                        amountPerPeriod
                    }

                schedules.add(
                    RepaymentSchedule(
                        transactionId = transaction.id!!,
                        scheduledDate = paymentDate,
                        scheduledAmount = amount,
                    ),
                )
            }
        }

        return schedules
    }

    private fun generateFixedMonthlySchedules(transaction: Transaction): List<RepaymentSchedule> {
        val monthlyAmount = transaction.monthlyAmount ?: return emptyList()
        val paymentDay = transaction.paymentDay ?: 1
        val totalAmount = transaction.totalAmount

        val schedules = mutableListOf<RepaymentSchedule>()
        var currentDate = transaction.createdAt.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
        var remainingAmount = totalAmount

        if (currentDate.dayOfMonth > paymentDay) {
            currentDate = currentDate.plusMonths(1).withDayOfMonth(paymentDay)
        } else {
            currentDate = currentDate.withDayOfMonth(paymentDay)
        }

        while (remainingAmount > BigDecimal.ZERO) {
            val paymentAmount = if (remainingAmount < monthlyAmount) remainingAmount else monthlyAmount

            schedules.add(
                RepaymentSchedule(
                    transactionId = transaction.id!!,
                    scheduledDate = currentDate,
                    scheduledAmount = paymentAmount,
                ),
            )

            remainingAmount -= paymentAmount
            currentDate = currentDate.plusMonths(1)
        }

        return schedules
    }

    private fun toTransactionResponseDTO(transaction: Transaction): TransactionResponseDTO =
        TransactionResponseDTO(
            id = transaction.id!!,
            transactionType = transaction.transactionType,
            counterpartName = transaction.counterpartName,
            counterpartCharacter = transaction.counterpartCharacter,
            relationship = transaction.relationship,
            customRelationship = transaction.customRelationship,
            transactionDate = transaction.transactionDate,
            totalAmount = transaction.totalAmount,
            completedAmount = transaction.completedAmount,
            remainingAmount = transaction.remainingAmount(),
            memo = transaction.memo,
            repaymentType = transaction.repaymentType,
            targetDate = transaction.targetDate,
            monthlyAmount = transaction.monthlyAmount,
            paymentDay = transaction.paymentDay,
            hasTargetDate = transaction.hasTargetDate,
            isCompleted = transaction.isCompleted(),
            createdAt = transaction.createdAt,
            updatedAt = transaction.updatedAt,
        )
}
