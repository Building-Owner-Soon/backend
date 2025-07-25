package com.bos.backend.presentation.auth.dto

import com.bos.backend.domain.user.enum.ProviderType

data class CheckEmailResponse(
    val email: String,
    val isExist: Boolean,
    val provider: ProviderType,
)
