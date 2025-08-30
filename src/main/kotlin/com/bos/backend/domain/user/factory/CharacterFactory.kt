package com.bos.backend.domain.user.factory

import com.bos.backend.application.service.CharacterAssetService
import com.bos.backend.domain.profile.enums.ProfileAssetType
import com.bos.backend.domain.user.entity.Character

object CharacterFactory {
    object DefaultIds {
        const val FACE = "FACE_TYPE_1"
        const val HAND = "HAND_TYPE_1"
        const val BANG = "BANG_TYPE_1"
        const val BACK_HAIR = "BACK_HAIR_TYPE_1"
        const val EYES = "EYES_TYPE_1"
        const val MOUTH = "MOUTH_TYPE_1"
        const val HOME = "HOME_TYPE_1"
    }

    object DefaultValues {
        const val SKIN_COLOR = "#FFFFFF"
    }

    suspend fun createDefaultCharacter(characterAssetService: CharacterAssetService): Character =
        Character(
            face = characterAssetService.createCharacterAsset(DefaultIds.FACE, ProfileAssetType.FACE),
            hand = characterAssetService.createCharacterAsset(DefaultIds.HAND, ProfileAssetType.HAND),
            skinColor = DefaultValues.SKIN_COLOR,
            bang = characterAssetService.createCharacterAsset(DefaultIds.BANG, ProfileAssetType.BANG),
            backHair = characterAssetService.createCharacterAsset(DefaultIds.BACK_HAIR, ProfileAssetType.BACK_HAIR),
            eyes = characterAssetService.createCharacterAsset(DefaultIds.EYES, ProfileAssetType.EYES),
            mouth = characterAssetService.createCharacterAsset(DefaultIds.MOUTH, ProfileAssetType.MOUTH),
            home = characterAssetService.createCharacterAsset(DefaultIds.HOME, ProfileAssetType.HOME),
        )
}
