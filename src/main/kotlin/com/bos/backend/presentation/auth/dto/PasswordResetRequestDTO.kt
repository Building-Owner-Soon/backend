package com.bos.backend.presentation.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class PasswordResetRequestDTO(
    @field:Email
    @field:NotBlank
    val email: String,
    @field:NotBlank
    val newPassword: String,
)
