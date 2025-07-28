package com.bos.backend.application.service

import com.bos.backend.domain.user.enum.EmailVerificationPurpose

interface EmailVerificationService {
    suspend fun sendVerificationEmail(
        email: String,
        purpose: EmailVerificationPurpose,
    )

    suspend fun verifyCode(
        email: String,
        verificationCode: String,
        purpose: EmailVerificationPurpose,
    ): Boolean

    suspend fun isEmailDuplicated(email: String): Boolean

    suspend fun isVerificationCodeExpired(
        email: String,
        purpose: EmailVerificationPurpose,
    ): Boolean

    suspend fun isVerificationCodeMatched(
        email: String,
        code: String,
        purpose: EmailVerificationPurpose,
    ): Boolean
}
