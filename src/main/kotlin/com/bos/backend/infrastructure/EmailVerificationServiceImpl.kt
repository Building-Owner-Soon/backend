package com.bos.backend.infrastructure

import com.bos.backend.application.service.EmailVerificationService
import com.bos.backend.domain.user.enum.EmailVerificationPurpose
import com.bos.backend.domain.user.repository.UserAuthRepository
import com.bos.backend.infrastructure.external.EmailVerificationCodeStore
import com.bos.backend.infrastructure.template.EmailTemplate
import com.bos.backend.infrastructure.util.EmailHelper
import org.springframework.stereotype.Service
import java.util.Random

@Service
class EmailVerificationServiceImpl(
    private val emailVerificationCodeStore: EmailVerificationCodeStore,
    private val userAuthRepository: UserAuthRepository,
    private val emailHelper: EmailHelper,
) : EmailVerificationService {
    companion object {
        private const val VERIFICATION_CODE_LENGTH = 6
        private const val VERIFICATION_CODE_DIGIT_RANGE = 10
    }

    override suspend fun sendVerificationEmail(
        email: String,
        purpose: EmailVerificationPurpose,
    ) {
        emailVerificationCodeStore.deleteVerificationCode(email, purpose.value)
        val verificationCode = generateVerificationCode()

        emailVerificationCodeStore.saveVerificationCode(email, verificationCode, purpose.value)
        val content = EmailTemplate.Verification.CONTENT.replace("{code}", verificationCode)

        emailHelper.sendEmail(
            to = email,
            subject = EmailTemplate.Verification.SUBJECT,
            content = content,
        )
    }

    override suspend fun verifyCode(
        email: String,
        verificationCode: String,
        purpose: EmailVerificationPurpose,
    ): Boolean {
        val savedCode = emailVerificationCodeStore.getVerificationCode(email, purpose.value)
        val isValid = savedCode != null && savedCode == verificationCode

        if (isValid) {
            emailVerificationCodeStore.deleteVerificationCode(email, purpose.value)
        }
        return isValid
    }

    override suspend fun isEmailDuplicated(email: String): Boolean = userAuthRepository.existsByEmail(email)

    override suspend fun isVerificationCodeExpired(
        email: String,
        purpose: EmailVerificationPurpose,
    ): Boolean = emailVerificationCodeStore.getVerificationCode(email, purpose.value) == null

    override suspend fun isVerificationCodeMatched(
        email: String,
        code: String,
        purpose: EmailVerificationPurpose,
    ): Boolean {
        val savedCode = emailVerificationCodeStore.getVerificationCode(email, purpose.value)
        return savedCode == code
    }

    private fun generateVerificationCode(): String {
        val random = Random()
        return (1..VERIFICATION_CODE_LENGTH).map { random.nextInt(VERIFICATION_CODE_DIGIT_RANGE) }.joinToString("")
    }
}
