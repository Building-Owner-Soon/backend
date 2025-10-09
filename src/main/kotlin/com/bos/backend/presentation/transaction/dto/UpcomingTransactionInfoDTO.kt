package com.bos.backend.presentation.transaction.dto

import java.time.LocalDate

data class UpcomingTransactionInfoDTO(
    val dueDate: LocalDate,
    val amount: Long,
)
