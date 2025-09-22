package com.bos.backend.presentation.auth.dto

import jakarta.validation.constraints.NotBlank

data class TokenRefreshRequestDTO(
    @field:NotBlank(message = "리프레시 토큰은 필수입니다.")
    val refreshToken: String,
)
