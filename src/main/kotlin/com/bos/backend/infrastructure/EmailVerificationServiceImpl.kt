package com.bos.backend.infrastructure

import com.bos.backend.application.service.EmailVerificationService
import com.bos.backend.domain.user.repository.UserAuthRepository
import com.bos.backend.infrastructure.external.EmailVerificationCodeStore
import com.bos.backend.infrastructure.template.EmailTemplate
import com.bos.backend.infrastructure.util.EmailHelper
import com.bos.backend.presentation.auth.dto.EmailVerificationRequestDTO
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

    override suspend fun sendVerificationEmail(request: EmailVerificationRequestDTO) {
        emailVerificationCodeStore.deleteVerificationCode(request.email)
        val verificationCode = generateVerificationCode()

        emailVerificationCodeStore.saveVerificationCode(request.email, verificationCode)
        val content = EmailTemplate.Verification.CONTENT.replace("{code}", verificationCode)

        // 이메일 전송
        emailHelper.sendEmail(
            to = request.email,
            subject = EmailTemplate.Verification.SUBJECT,
            content = content,
        )
    }

    override suspend fun verifyEmail(
        email: String,
        verificationCode: String,
    ): Boolean {
        val savedCode = emailVerificationCodeStore.getVerificationCode(email)
        val isValid = savedCode != null && savedCode == verificationCode

        if (isValid) {
            emailVerificationCodeStore.deleteVerificationCode(email)
        }
        return isValid
    }

    override suspend fun isEmailDuplicated(email: String): Boolean = userAuthRepository.existsByEmail(email)

    override suspend fun isVerificationCodeExpired(email: String): Boolean =
        emailVerificationCodeStore.getVerificationCode(email) == null

    override suspend fun isVerificationCodeMatched(
        email: String,
        code: String,
    ): Boolean {
        val savedCode = emailVerificationCodeStore.getVerificationCode(email)
        return savedCode == code
    }

    private fun generateVerificationCode(): String {
        val random = Random()
        return (1..VERIFICATION_CODE_LENGTH).map { random.nextInt(VERIFICATION_CODE_DIGIT_RANGE) }.joinToString("")
    }
}
