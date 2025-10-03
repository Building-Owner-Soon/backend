package com.bos.backend.presentation.transaction.dto

data class TransactionSummaryDTO(
    val totalAmount: Long,
    val completedAmount: Long,
    val remainingAmount: Long,
)
