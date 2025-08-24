package com.bos.backend.application.user

import com.bos.backend.application.CustomException
import com.bos.backend.application.auth.AuthErrorCode
import com.bos.backend.application.mapper.CharacterAssets
import com.bos.backend.application.mapper.CharacterMapper
import com.bos.backend.application.mapper.UserMapper
import com.bos.backend.application.service.CharacterAssetService
import com.bos.backend.domain.profile.enums.ProfileAssetType
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
    private val characterAssetService: CharacterAssetService,
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

            val character =
                updateUserRequestDTO.character?.let { characterDTO ->
                    val currentCharacter = user.character

                    // TODO: nullable 처리 로직 개선 필요, 유저 생성시 캐릭터 기본값 설정, 중구난방 DTO 정리 필요
                    val assets =
                        CharacterAssets(
                            faceShape =
                                characterDTO.face?.let {
                                    characterAssetService.createCharacterAsset(it, ProfileAssetType.FACE)
                                } ?: currentCharacter?.face
                                    ?: characterAssetService.createCharacterAsset("FACE_TYPE_1", ProfileAssetType.FACE),
                            hand =
                                characterDTO.hand?.let {
                                    characterAssetService.createCharacterAsset(it, ProfileAssetType.HAND)
                                } ?: currentCharacter?.hand
                                    ?: characterAssetService.createCharacterAsset("HAND_TYPE_1", ProfileAssetType.HAND),
                            frontHair =
                                characterDTO.bang?.let {
                                    characterAssetService.createCharacterAsset(it, ProfileAssetType.BANG)
                                } ?: currentCharacter?.bang
                                    ?: characterAssetService.createCharacterAsset("BANG_TYPE_1", ProfileAssetType.BANG),
                            backHair =
                                characterDTO.backHair?.let {
                                    characterAssetService.createCharacterAsset(it, ProfileAssetType.BACK_HAIR)
                                } ?: currentCharacter?.backHair
                                    ?: characterAssetService.createCharacterAsset(
                                        "BACK_HAIR_TYPE_1",
                                        ProfileAssetType.BACK_HAIR,
                                    ),
                            eyes =
                                characterDTO.eyes?.let {
                                    characterAssetService.createCharacterAsset(it, ProfileAssetType.EYES)
                                } ?: currentCharacter?.eyes
                                    ?: characterAssetService.createCharacterAsset("EYES_TYPE_1", ProfileAssetType.EYES),
                            mouth =
                                characterDTO.mouth?.let {
                                    characterAssetService.createCharacterAsset(it, ProfileAssetType.MOUTH)
                                } ?: currentCharacter?.mouth
                                    ?: characterAssetService.createCharacterAsset(
                                        "MOUTH_TYPE_1",
                                        ProfileAssetType.MOUTH,
                                    ),
                        )

                    characterMapper.toCharacter(
                        assets = assets,
                        skinColor = characterDTO.skinColor ?: currentCharacter?.skinColor ?: "#FFFFFF",
                    )
                }

            val updatedUser =
                user
                    .update(
                        nickname = updateUserRequestDTO.nickname,
                        isNotificationAllowed = updateUserRequestDTO.isNotificationAllowed,
                        isMarketingAgreed = updateUserRequestDTO.isMarketingAgreed,
                        character = character,
                        homeType = updateUserRequestDTO.homeType,
                    ).let {
                        userRepository.save(it)
                    }

            userMapper.toUserProfileDTO(updatedUser)
        }
}
