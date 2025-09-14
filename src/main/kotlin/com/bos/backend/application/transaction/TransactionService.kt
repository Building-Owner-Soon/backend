package com.bos.backend.application.transaction

import com.bos.backend.application.CommonErrorCode
import com.bos.backend.application.CustomException
import com.bos.backend.application.auth.AuthErrorCode
import com.bos.backend.domain.transaction.entity.Transaction
import com.bos.backend.domain.transaction.repository.TransactionRepository
import com.bos.backend.domain.user.repository.UserRepository
import com.bos.backend.presentation.transaction.dto.CreateTransactionRequestDTO
import com.bos.backend.presentation.transaction.dto.TransactionResponseDTO
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import java.math.BigDecimal

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val transactionalOperator: TransactionalOperator,
) {
    suspend fun createTransaction(
        userId: Long,
        createTransactionRequestDTO: CreateTransactionRequestDTO,
    ): TransactionResponseDTO =
        transactionalOperator.executeAndAwait {
            userRepository.findById(userId)
                ?: throw CustomException(AuthErrorCode.USER_NOT_FOUND)

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
            toTransactionResponseDTO(savedTransaction)
        }

    suspend fun getTransactionDetail(
        userId: Long,
        transactionId: Long,
    ): TransactionResponseDTO {
        userRepository.findById(userId)
            ?: throw CustomException(AuthErrorCode.USER_NOT_FOUND)

        val transaction =
            transactionRepository.findById(transactionId)
                ?: throw CustomException(CommonErrorCode.RESOURCE_NOT_FOUND)

        if (transaction.userId != userId) {
            throw CustomException(CommonErrorCode.RESOURCE_NOT_FOUND)
        }

        return toTransactionResponseDTO(transaction)
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
