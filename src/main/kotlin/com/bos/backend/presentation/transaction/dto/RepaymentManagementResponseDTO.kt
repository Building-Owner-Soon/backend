package com.bos.backend.presentation.transaction.dto

data class RepaymentManagementResponseDTO(
    val overdueRepayments: List<RepaymentScheduleItemDTO>,
    val regularRepayments: List<RepaymentScheduleItemDTO>,
)
