package com.bos.backend.application.user

import com.bos.backend.application.CustomException
import com.bos.backend.application.auth.AuthErrorCode
import com.bos.backend.application.mapper.CharacterMapper
import com.bos.backend.application.mapper.UserMapper
import com.bos.backend.domain.user.repository.UserRepository
import com.bos.backend.presentation.user.dto.UpdateUserRequestDTO
import com.bos.backend.presentation.user.dto.UserProfileDTO
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

@Service
class UserService(
    private val userRepository: UserRepository,
    private val transactionalOperator: TransactionalOperator,
    private val userMapper: UserMapper,
    private val characterMapper: CharacterMapper,
) {
    suspend fun getUserProfile(userId: Long): UserProfileDTO {
        val user =
            userRepository.findById(userId)
                ?: throw CustomException(AuthErrorCode.USER_NOT_FOUND)

        return userMapper.toUserProfileDTO(user)
    }

    suspend fun updateUserProfile(
        userId: Long,
        updateUserRequestDTO: UpdateUserRequestDTO,
    ): UserProfileDTO =
        transactionalOperator.executeAndAwait {
            val user =
                userRepository.findById(userId)
                    ?: throw NoSuchElementException("User with ID $userId not found")

            val updatedUser =
                user
                    .update(
                        nickname = updateUserRequestDTO.nickname,
                        isNotificationAllowed = updateUserRequestDTO.isNotificationAllowed,
                        isMarketingAgreed = updateUserRequestDTO.isMarketingAgreed,
                        character = updateUserRequestDTO.character?.let { characterMapper.toCharacter(it) },
                        homeType = updateUserRequestDTO.homeType,
                    ).let {
                        userRepository.save(it)
                    }

            userMapper.toUserProfileDTO(updatedUser)
        }
}
