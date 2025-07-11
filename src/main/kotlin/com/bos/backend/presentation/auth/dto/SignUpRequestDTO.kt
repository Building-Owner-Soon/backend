package com.bos.backend.presentation.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class SignUpRequestDTO(
    @field:NotBlank
    val provider: String,
    @field:Email
    @field:NotBlank
    val email: String,
    val providerId: String? = null,
    val password: String? = null,
    val providerAccessToken: String? = null,
    @field:NotEmpty
    val termsAgreements: List<TermAgreementItemDTO>,
)
