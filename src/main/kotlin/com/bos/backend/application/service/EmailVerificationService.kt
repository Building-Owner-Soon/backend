package com.bos.backend.application.service

import com.bos.backend.presentation.auth.dto.EmailVerificationRequestDTO

interface EmailVerificationService {
    suspend fun sendVerificationEmail(request: EmailVerificationRequestDTO)

    suspend fun verifyEmail(
        email: String,
        verificationCode: String,
    ): Boolean

    suspend fun isEmailDuplicated(email: String): Boolean

    suspend fun isVerificationCodeExpired(email: String): Boolean

    suspend fun isVerificationCodeMatched(
        email: String,
        code: String,
    ): Boolean
}
