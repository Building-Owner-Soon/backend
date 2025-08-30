package com.bos.backend.application.auth.strategy

import com.bos.backend.application.service.CharacterAssetService
import com.bos.backend.domain.user.entity.User
import com.bos.backend.domain.user.entity.UserAuth
import com.bos.backend.domain.user.enum.ProviderType
import com.bos.backend.domain.user.factory.CharacterFactory
import com.bos.backend.domain.user.repository.UserAuthRepository
import com.bos.backend.domain.user.repository.UserRepository
import com.bos.backend.presentation.auth.dto.SignInRequestDTO
import com.bos.backend.presentation.auth.dto.SignUpRequestDTO
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class BosAuthStrategy(
    private val userRepository: UserRepository,
    private val userAuthRepository: UserAuthRepository,
    private val characterAssetService: CharacterAssetService,
) : AuthStrategy {
    override val providerType: ProviderType
        get() = ProviderType.BOS

    override suspend fun signUp(request: SignUpRequestDTO): AuthResult {
        requireNotNull(request.email) { "Email is required for signup" }
        requireNotNull(request.password) { "Password is required for BOS signup" }

        require(!userAuthRepository.existsByEmail(request.email)) { "User already exists with email: ${request.email}" }

        val user =
            userRepository.save(
                User(
                    // TODO: random nickname generator
                    nickname = "닉네임 임시",
                    character = CharacterFactory.createDefaultCharacter(characterAssetService),
                    isNotificationAllowed = false,
                ),
            )

        val userAuth =
            userAuthRepository.save(
                // TODO: providerType에 따른 생성 제어 방법 고민
                UserAuth(
                    null,
                    userId = user.id!!,
                    _providerType = providerType.value,
                    email = request.email,
                    passwordHash = request.password,
                    providerId = null,
                    lastLoginAt = null,
                ),
            )

        return AuthResult(user, userAuth)
    }

    override suspend fun signIn(request: SignInRequestDTO): AuthResult {
        requireNotNull(request.password) { "Password is required for BOS signin" }

        val userAuth =
            userAuthRepository.findByEmailAndProviderType(request.email, providerType.value)
                ?: throw IllegalArgumentException("User not found with email: ${request.email}")

        require(userAuth.passwordHash == request.password) { "Invalid password for user: ${request.email}" }

        val user = checkNotNull(userRepository.findById(userAuth.userId)) { "User not found" }

        userAuthRepository.updateLastLoginAt(userAuth.id!!)

        return AuthResult(user, userAuth.copy(lastLoginAt = Instant.now()))
    }
}
