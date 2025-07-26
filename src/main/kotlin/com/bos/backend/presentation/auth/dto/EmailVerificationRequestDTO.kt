package com.bos.backend.presentation.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class EmailVerificationRequestDTO(
    @field:Email
    @field:NotBlank
    val email: String,
)

data class EmailVerificationCheckDTO(
    @field:Email
    @field:NotBlank
    val email: String,
    @field:NotBlank
    val code: String,
)
