package com.bos.backend.presentation.auth.dto

data class CommonSignResponseDTO(
    val accessToken: String,
    val refreshToken: String,
)
