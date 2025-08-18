package com.bos.backend.application.user

import com.bos.backend.application.CustomException
import com.bos.backend.application.auth.AuthErrorCode
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
) {
    suspend fun getUserProfile(userId: Long): UserProfileDTO {
        val user =
            userRepository.findById(userId)
                ?: throw CustomException(AuthErrorCode.USER_NOT_FOUND)

        // TODO: mapstruct 도입
        return UserProfileDTO(
            id = checkNotNull(user.id) { "User ID is null" },
            nickname = user.nickname ?: "Unknown",
            characterComponents = user.characterComponents,
            homeType = user.homeType,
            isNotificationAllowed = user.isNotificationAllowed,
            isMarketingAgreed = user.isMarketingAgreed,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt,
        )
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
                        characterComponents = updateUserRequestDTO.characterComponents,
                        homeType = updateUserRequestDTO.homeType,
                    ).let {
                        userRepository.save(it)
                    }

            // TODO: mapstruct 도입
            UserProfileDTO(
                id = checkNotNull(updatedUser.id) { "User ID is null" },
                nickname = updatedUser.nickname,
                characterComponents = updatedUser.characterComponents,
                homeType = updatedUser.homeType,
                isNotificationAllowed = updatedUser.isNotificationAllowed,
                isMarketingAgreed = updatedUser.isMarketingAgreed,
                createdAt = updatedUser.createdAt,
                updatedAt = updatedUser.updatedAt,
            )
        }
}
