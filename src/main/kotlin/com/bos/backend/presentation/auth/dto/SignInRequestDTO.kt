package com.bos.backend.presentation.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class SignInRequestDTO(
    @field:NotBlank
    val provider: String,
    @field:Email
    @field:NotBlank
    val email: String,
    val providerId: String? = null,
    val password: String? = null,
    val providerAccessToken: String? = null,
)
