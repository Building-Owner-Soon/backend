package com.bos.backend.presentation.auth.dto

import com.bos.backend.presentation.auth.dto.validation.ValidPassword
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class PasswordResetRequestDTO(
    @field:Email
    @field:NotBlank
    val email: String,
    
    @field:NotBlank
    @field:ValidPassword
    val newPassword: String,
)
