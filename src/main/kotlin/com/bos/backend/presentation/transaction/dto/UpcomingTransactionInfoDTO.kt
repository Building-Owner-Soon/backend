package com.bos.backend.presentation.transaction.dto

import java.time.LocalDate

data class UpcomingTransactionInfoDTO(
    val paymentType: PaymentType,
    val dueDate: LocalDate,
    val amount: Long,
)

enum class PaymentType(val description: String) {
    REPAYMENT("갚을돈"),
    RECEIVABLE("받을돈"),
}
