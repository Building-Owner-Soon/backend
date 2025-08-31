package com.bos.backend.application.user

import com.bos.backend.application.CustomException
import com.bos.backend.application.auth.AuthErrorCode
import com.bos.backend.application.builder.CharacterBuilder
import com.bos.backend.application.mapper.UserMapper
import com.bos.backend.domain.user.repository.UserAuthRepository
import com.bos.backend.domain.user.repository.UserRepository
import com.bos.backend.presentation.user.dto.UpdateUserRequestDTO
import com.bos.backend.presentation.user.dto.UserProfileResponseDTO
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userAuthRepository: UserAuthRepository,
    private val transactionalOperator: TransactionalOperator,
    private val userMapper: UserMapper,
    private val characterBuilder: CharacterBuilder,
) {
    suspend fun getUserProfile(userId: Long): UserProfileResponseDTO {
        val user =
            userRepository.findById(userId)
                ?: throw CustomException(AuthErrorCode.USER_NOT_FOUND)
        val userAuth =
            userAuthRepository.findByUserId(userId)
                ?: throw CustomException(AuthErrorCode.USERAUTH_NOT_FOUND)

        return userMapper.toUserProfileDTO(user, userAuth)
    }

    @Suppress("LongMethod", "ComplexMethod")
    suspend fun updateUserProfile(
        userId: Long,
        updateUserRequestDTO: UpdateUserRequestDTO,
    ): UserProfileResponseDTO =
        transactionalOperator.executeAndAwait {
            val user =
                userRepository.findById(userId)
                    ?: throw NoSuchElementException("User with ID $userId not found")

            val character =
                updateUserRequestDTO.character?.let { characterDTO ->
                    characterBuilder.buildCharacter(characterDTO, user.character)
                }

            val updatedUser =
                user
                    .update(
                        nickname = updateUserRequestDTO.nickname,
                        isNotificationAllowed = updateUserRequestDTO.isNotificationAllowed,
                        isMarketingAgreed = updateUserRequestDTO.isMarketingAgreed,
                        character = character,
                    ).let {
                        userRepository.save(it)
                    }
            val userAuth =
                userAuthRepository.findByUserId(userId)
                    ?: throw NoSuchElementException("UserAuth with User ID $userId not found")

            userMapper.toUserProfileDTO(updatedUser, userAuth)
        }
}
