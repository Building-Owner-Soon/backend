package com.bos.backend.application.auth.strategy

import com.bos.backend.domain.user.entity.User
import com.bos.backend.domain.user.entity.UserAuth
import com.bos.backend.domain.user.enum.ProviderType
import com.bos.backend.domain.user.repository.UserAuthRepository
import com.bos.backend.domain.user.repository.UserRepository
import com.bos.backend.infrastructure.util.NicknameGenerator
import com.bos.backend.presentation.auth.dto.SignInRequestDTO
import com.bos.backend.presentation.auth.dto.SignUpRequestDTO
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class BosAuthStrategy(
    private val userRepository: UserRepository,
    private val userAuthRepository: UserAuthRepository,
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
                    nickname = NicknameGenerator.generateRandomNickname(),
                    allowNotification = false,
                ),
            )

        val userAuth =
            userAuthRepository.save(
                UserAuth.createForBosProvider(
                    userId = user.id!!,
                    email = request.email,
                    passwordHash = request.password,
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
