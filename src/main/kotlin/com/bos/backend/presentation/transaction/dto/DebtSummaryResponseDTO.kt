package com.bos.backend.presentation.transaction.dto

data class DebtSummaryResponseDTO(
    val debtSummary: DebtSummaryDTO,
)

data class DebtSummaryDTO(
    val lendSummary: TransactionSummaryDTO,
    val borrowSummary: TransactionSummaryDTO,
)
