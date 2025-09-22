package com.bos.backend.presentation.transaction.dto

import com.bos.backend.domain.transaction.enum.RepaymentStatus
import java.math.BigDecimal
import java.time.LocalDate

data class RepaymentScheduleItemDTO(
    val status: RepaymentStatus,
    val displayDate: LocalDate,
    val displayAmount: BigDecimal,
)
