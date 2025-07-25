package com.bos.backend.presentation.auth.dto

data class EmailVerificationCodeRequestDTO(
    val email: String,
    val code: String? = null,
)
