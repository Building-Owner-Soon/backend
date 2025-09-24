package com.bos.backend.presentation.auth.dto

import com.bos.backend.presentation.auth.dto.validation.ValidPassword
import jakarta.validation.constraints.NotBlank

data class PasswordChangeRequestDTO(
    @field:NotBlank
    val currentPassword: String,
    @field:NotBlank
    @field:ValidPassword
    val newPassword: String,
)
