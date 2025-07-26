package com.bos.backend.infrastructure.event

data class EmailVerificationEvent(
    val email: String,
    val subject: String,
    val content: String,
) 