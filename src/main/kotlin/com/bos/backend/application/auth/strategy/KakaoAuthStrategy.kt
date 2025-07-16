package com.bos.backend.application.auth.strategy

import com.bos.backend.domain.user.entity.User
import com.bos.backend.domain.user.entity.UserAuth
import com.bos.backend.domain.user.enum.ProviderType
import com.bos.backend.domain.user.repository.UserAuthRepository
import com.bos.backend.domain.user.repository.UserRepository
import com.bos.backend.infrastructure.external.KakaoApiService
import com.bos.backend.presentation.auth.dto.SignInRequestDTO
import com.bos.backend.presentation.auth.dto.SignUpRequestDTO
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class KakaoAuthStrategy(
    private val userRepository: UserRepository,
    private val userAuthRepository: UserAuthRepository,
    private val kakaoApiService: KakaoApiService,
) : AuthStrategy {
    override val providerType: ProviderType = ProviderType.KAKAO

    override suspend fun signUp(request: SignUpRequestDTO): AuthResult {
        requireNotNull(request.email) { "Email is required for signup" }
        requireNotNull(request.providerId) { "Provider ID is required for Kakao signup" }
        requireNotNull(request.providerAccessToken) { "Provider access token is required for Kakao signup" }

        // 카카오 토큰 검증
        if (!validateProviderToken(request.providerAccessToken, request.providerId, request.email)) {
            throw IllegalArgumentException("Invalid Kakao token or user info mismatch")
        }

        // 이미 가입된 사용자인지 확인
        if (userAuthRepository.findByProviderIdAndProviderType(request.providerId, providerType.value) != null) {
            throw IllegalArgumentException("User already exists with email: ${request.email}")
        }

        // 사용자 생성
        val user =
            userRepository.save(
                User(nickname = "임시 닉네임", allowNotification = false),
            )

        // 인증 정보 저장
        val userAuth =
            userAuthRepository.save(
                // TODO: providerType에 따른 생성 제어 방법 고민
                UserAuth(
                    userId = user.id!!,
                    _providerType = providerType.value,
                    providerId = request.providerId,
                    email = request.email,
                ),
            )

        return AuthResult(user, userAuth)
    }

    override suspend fun signIn(request: SignInRequestDTO): AuthResult {
        requireNotNull(request.providerId) { "Provider ID is required for Kakao signin" }
        requireNotNull(request.providerAccessToken) { "Provider access token is required for Kakao signin" }

        // 카카오 토큰 검증
        if (!validateProviderToken(request.providerAccessToken, request.providerId, request.email)) {
            throw IllegalArgumentException("Invalid Kakao token or user info mismatch")
        }

        val userAuth =
            userAuthRepository.findByProviderIdAndProviderType(request.providerId, providerType.value)
                ?: throw IllegalArgumentException("User not found with email: ${request.email}")

        val user =
            userRepository.findById(userAuth.userId)
                ?: throw IllegalStateException("User not found")

        // 로그인 시간 업데이트
        userAuthRepository.updateLastLoginAt(userAuth.id!!)

        return AuthResult(user, userAuth.copy(lastLoginAt = Instant.now()))
    }

    private suspend fun validateProviderToken(
        token: String,
        providerId: String?,
        email: String,
    ): Boolean =
        try {
            val kakaoUserInfo = kakaoApiService.getUserInfo(token)
            kakaoUserInfo.id == providerId && kakaoUserInfo.email == email
        } catch (e: Exception) {
            false
        }
}
