package com.bos.backend.presentation.transaction.dto

import com.bos.backend.domain.transaction.enum.RelationshipType
import com.bos.backend.domain.transaction.enum.RepaymentType
import com.bos.backend.domain.transaction.enum.TransactionType
import com.bos.backend.domain.user.entity.Character
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import java.math.BigDecimal
import java.time.LocalDate

data class CreateTransactionRequestDTO(
    @field:NotNull(message = "거래 유형은 필수입니다")
    val transactionType: TransactionType,
    @field:NotBlank(message = "상대방 이름은 필수입니다")
    val counterpartName: String,
    @field:NotNull(message = "상대방 캐릭터는 필수입니다")
    val counterpartCharacter: Character,
    @field:NotNull(message = "관계는 필수입니다")
    val relationship: RelationshipType,
    val customRelationship: String?,
    @field:NotNull(message = "거래일은 필수입니다")
    val transactionDate: LocalDate,
    @field:NotNull(message = "전체 금액은 필수입니다")
    @field:DecimalMin(value = "0.01", message = "전체 금액은 0보다 커야 합니다")
    val totalAmount: BigDecimal,
    @field:PositiveOrZero(message = "완료된 금액은 0 이상이어야 합니다")
    val completedAmount: BigDecimal?,
    val memo: String?,
    @field:NotNull(message = "상환 유형은 필수입니다")
    val repaymentType: RepaymentType,
    val targetDate: LocalDate?,
    @field:PositiveOrZero(message = "월 납부 금액은 0 이상이어야 합니다")
    val monthlyAmount: BigDecimal?,
    val paymentDay: Int?,
    val hasTargetDate: Boolean?,
)
