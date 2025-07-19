package com.bos.backend.presentation.auth.dto

import jakarta.validation.constraints.NotNull

data class TermAgreementItemDTO(
    @field:NotNull
    val termId: Long,
    @field:NotNull
    val isAgree: Boolean,
)
