package com.bos.backend.application.service

import com.bos.backend.domain.auth.enums.EmailVerificationType

interface EmailVerificationService {
    suspend fun sendVerificationEmail(
        email: String,
        type: EmailVerificationType,
    )

    suspend fun verifyCode(
        email: String,
        verificationCode: String,
        type: EmailVerificationType,
    ): Boolean

    suspend fun isEmailDuplicated(email: String): Boolean

    suspend fun isVerificationCodeExpired(
        email: String,
        type: EmailVerificationType,
    ): Boolean

    suspend fun isVerificationCodeMatched(
        email: String,
        code: String,
        type: EmailVerificationType,
    ): Boolean

    suspend fun isResendAllowed(
        email: String,
        type: EmailVerificationType,
    ): Boolean
}
