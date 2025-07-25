package com.bos.backend.application.auth

import com.bos.backend.application.auth.strategy.AuthStrategyResolver
import com.bos.backend.application.service.EmailVerificationService
import com.bos.backend.application.service.JwtService
import com.bos.backend.domain.term.entity.UserTermAgreement
import com.bos.backend.domain.term.repository.UserTermAgreementRepository
import com.bos.backend.presentation.auth.dto.CommonSignResponseDTO
import com.bos.backend.presentation.auth.dto.SignInRequestDTO
import com.bos.backend.presentation.auth.dto.SignUpRequestDTO
import com.bos.backend.presentation.auth.dto.EmailVerificationRequestDTO
import com.bos.backend.presentation.auth.dto.EmailVerificationResponseDTO
import com.bos.backend.presentation.auth.dto.EmailVerificationCheckDTO
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService(
    private val authStrategyResolver: AuthStrategyResolver,
    private val jwtService: JwtService,
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

    suspend fun sendVerificationEmail(request: EmailVerificationRequestDTO): EmailVerificationResponseDTO {
        emailVerificationService.sendVerificationEmail(request.email)
        return EmailVerificationResponseDTO()
    }

    suspend fun verifyEmail(request: EmailVerificationCheckDTO): Boolean {
        return emailVerificationService.verifyEmail(request.email, request.code)
    }
}
