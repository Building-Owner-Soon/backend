package com.bos.backend.application.auth

import com.bos.backend.application.CustomException
import com.bos.backend.application.auth.strategy.AuthStrategyResolver
import com.bos.backend.application.service.EmailVerificationService
import com.bos.backend.application.service.JwtService
import com.bos.backend.domain.term.entity.UserTermAgreement
import com.bos.backend.domain.term.repository.UserTermAgreementRepository
import com.bos.backend.domain.user.enum.ProviderType
import com.bos.backend.domain.user.repository.UserAuthRepository
import com.bos.backend.presentation.auth.dto.CheckEmailResponse
import com.bos.backend.presentation.auth.dto.CommonSignResponseDTO
import com.bos.backend.presentation.auth.dto.EmailVerificationCheckDTO
import com.bos.backend.presentation.auth.dto.EmailVerificationRequestDTO
import com.bos.backend.presentation.auth.dto.SignInRequestDTO
import com.bos.backend.presentation.auth.dto.SignUpRequestDTO
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
@Suppress("LongParameterList")
class AuthService(
    private val authStrategyResolver: AuthStrategyResolver,
    private val jwtService: JwtService,
    private val userAuthRepository: UserAuthRepository,
    private val userTermsAgreementRepository: UserTermAgreementRepository,
    private val emailVerificationService: EmailVerificationService,
    @Value("\${application.jwt.access-token-expiration}") private val accessTokenExpiration: Long,
    @Value("\${application.jwt.refresh-token-expiration}") private val refreshTokenExpiration: Long,
) {
    suspend fun signUp(request: SignUpRequestDTO): CommonSignResponseDTO {
        val strategy = authStrategyResolver.resolve(request.provider)
        val authResult = strategy.signUp(request)

        // 약관 동의 저장
        // TODO: 약관 validation
        request.termsAgreements
            .filter { it.isAgree }
            .map {
                UserTermAgreement(
                    userId = authResult.user.id!!,
                    termsId = it.termId,
                )
            }.let { agreements ->
                userTermsAgreementRepository.saveAll(agreements)
            }

        val accessToken = jwtService.generateToken(authResult.user.id.toString(), accessTokenExpiration)
        val refreshToken = jwtService.generateToken(authResult.user.id.toString(), refreshTokenExpiration)

        return CommonSignResponseDTO(accessToken, refreshToken)
    }

    suspend fun signIn(request: SignInRequestDTO): CommonSignResponseDTO {
        val strategy = authStrategyResolver.resolve(request.provider)
        val authResult = strategy.signIn(request)

        val accessToken = jwtService.generateToken(authResult.user.id.toString(), accessTokenExpiration)
        val refreshToken = jwtService.generateToken(authResult.user.id.toString(), refreshTokenExpiration)

        return CommonSignResponseDTO(accessToken, refreshToken)
    }

    suspend fun sendVerificationEmail(request: EmailVerificationRequestDTO) {
        if (emailVerificationService.isEmailDuplicated(request.email)) {
            throw CustomException(
                AuthErrorCode.EMAIL_DUPLICATE.name,
                AuthErrorCode.EMAIL_DUPLICATE.status,
            )
        }

        emailVerificationService.sendVerificationEmail(request)
    }

    suspend fun verifyEmail(request: EmailVerificationCheckDTO) {
        if (emailVerificationService.isVerificationCodeExpired(request.email)) {
            throw CustomException(
                AuthErrorCode.EMAIL_VERIFICATION_CODE_EXPIRED.name,
                AuthErrorCode.EMAIL_VERIFICATION_CODE_EXPIRED.status,
            )
        }
        if (!emailVerificationService.isVerificationCodeMatched(request.email, request.code)) {
            throw CustomException(
                AuthErrorCode.EMAIL_VERIFICATION_CODE_MISMATCH.name,
                AuthErrorCode.EMAIL_VERIFICATION_CODE_MISMATCH.status,
            )
        }
        emailVerificationService.verifyEmail(request.email, request.code)
    }

    suspend fun isBosEmailUserAbsent(email: String): CheckEmailResponse {
        val userAuth =
            userAuthRepository.findByEmailAndProviderType(email, providerType = ProviderType.BOS.value)
                ?: throw NoSuchElementException("No user found with email: $email")
        return CheckEmailResponse(
            email = userAuth.email,
            isExist = true,
            provider = userAuth.providerType,
        )
    }
}
