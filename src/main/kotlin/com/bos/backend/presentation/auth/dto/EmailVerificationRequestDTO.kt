package com.bos.backend.presentation.auth.dto

import com.bos.backend.domain.auth.enum.EmailVerificationType
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class EmailVerificationRequestDTO(
    @field:Email
    @field:NotBlank
    val email: String,
    @field:NotNull
    val type: EmailVerificationType,
)

data class EmailVerificationCheckDTO(
    @field:Email
    @field:NotBlank
    val email: String,
    @field:NotBlank
    val code: String,
    @field:NotNull
    val type: EmailVerificationType,
)
