package com.bos.backend.presentation.auth.dto

import com.bos.backend.domain.auth.enums.EmailVerificationType
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class EmailVerificationRequestDTO(
    @field:Email
    @field:NotBlank
    val email: String,
    @field:NotBlank
    val type: EmailVerificationType,
)

data class EmailVerificationCheckDTO(
    @field:Email
    @field:NotBlank
    val email: String,
    @field:NotBlank
    val code: String,
    @field:NotBlank
    val type: EmailVerificationType,
)
