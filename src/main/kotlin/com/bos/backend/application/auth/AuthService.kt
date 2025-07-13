package com.bos.backend.application.auth

import com.bos.backend.application.service.JwtService
import com.bos.backend.domain.term.entity.UserTermAgreement
import com.bos.backend.domain.term.repository.TermRepository
import com.bos.backend.domain.term.repository.UserTermAgreementRepository
import com.bos.backend.domain.user.repository.UserAuthRepository
import com.bos.backend.domain.user.repository.UserRepository
import com.bos.backend.presentation.auth.dto.CommonSignResponseDTO
import com.bos.backend.presentation.auth.dto.SignInRequestDTO
import com.bos.backend.presentation.auth.dto.SignUpRequestDTO
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService(
    private val authStrategyResolver: AuthStrategyResolver,
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val userAuthRepository: UserAuthRepository,
    private val termsRepository: TermRepository,
    private val userTermsAgreementRepository: UserTermAgreementRepository,
    @Value("\${application.jwt.access-token-expiration}") private val accessTokenExpiration: Long,
    @Value("\${application.jwt.refresh-token-expiration}") private val refreshTokenExpiration: Long,
) {
    suspend fun signUp(request: SignUpRequestDTO): CommonSignResponseDTO {
        val terms = termsRepository.findAllByIds(request.termsAgreements.map { it.termId })

        val requiredTerms = terms.filter { it.isRequired }
        val agreedRequiredTerms =
            request.termsAgreements
                .filter { it.isAgree }
                .mapNotNull { agreement -> terms.find { it.id == agreement.termId } }
                .filter { it.isRequired }

        val strategy = authStrategyResolver.resolve(request.provider)
        val authResult = strategy.signUp(request)

        // 약관 동의 저장
        val agreements =
            request.termsAgreements
                .filter { it.isAgree }
                .map {
                    UserTermAgreement(
                        userId = authResult.user.id!!,
                        termsId = it.termId,
                    )
                }
        userTermsAgreementRepository.saveAll(agreements)

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
}
