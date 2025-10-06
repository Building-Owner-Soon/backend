package com.bos.backend.presentation.transaction.dto

import com.bos.backend.domain.transaction.enum.RelationshipType
import com.bos.backend.domain.transaction.enum.RepaymentType
import com.bos.backend.domain.transaction.enum.TransactionType
import com.bos.backend.domain.user.entity.Character
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

data class TransactionResponseDTO(
    val id: Long,
    val transactionType: TransactionType,
    val counterpartName: String,
    val counterpartCharacter: Character,
    val relationship: RelationshipType,
    val customRelationship: String?,
    val transactionDate: LocalDate,
    val totalAmount: BigDecimal,
    val completedAmount: BigDecimal,
    val remainingAmount: BigDecimal,
    val memo: String?,
    val repaymentType: RepaymentType,
    val targetDate: LocalDate?,
    val monthlyAmount: BigDecimal?,
    val paymentDay: Int?,
    val hasTargetDate: Boolean?,
    val createdAt: Instant,
    val updatedAt: Instant,
)
