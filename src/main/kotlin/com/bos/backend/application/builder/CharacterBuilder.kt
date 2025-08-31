package com.bos.backend.application.builder

import com.bos.backend.application.service.CharacterAssetService
import com.bos.backend.domain.profile.enums.ProfileAssetType
import com.bos.backend.domain.user.entity.Character
import com.bos.backend.domain.user.entity.CharacterAsset
import com.bos.backend.domain.user.factory.CharacterFactory
import com.bos.backend.presentation.user.dto.UpdateCharacterDTO
import org.springframework.stereotype.Component

@Component
class CharacterBuilder(
    private val characterAssetService: CharacterAssetService,
) {
    suspend fun buildCharacter(
        characterDTO: UpdateCharacterDTO,
        currentCharacter: Character?,
    ): Character {
        val baseCharacter = currentCharacter ?: CharacterFactory.createDefaultCharacter(characterAssetService)

        return Character(
            face =
                buildCharacterAsset(
                    dtoValue = characterDTO.face,
                    currentAsset = baseCharacter.face,
                    assetType = ProfileAssetType.FACE,
                ),
            hand =
                buildCharacterAsset(
                    dtoValue = characterDTO.hand,
                    currentAsset = baseCharacter.hand,
                    assetType = ProfileAssetType.HAND,
                ),
            skinColor =
                characterDTO.skinColor ?: baseCharacter.skinColor,
            bang =
                buildCharacterAsset(
                    dtoValue = characterDTO.bang,
                    currentAsset = baseCharacter.bang,
                    assetType = ProfileAssetType.BANG,
                ),
            backHair =
                buildCharacterAsset(
                    dtoValue = characterDTO.backHair,
                    currentAsset = baseCharacter.backHair,
                    assetType = ProfileAssetType.BACK_HAIR,
                ),
            eyes =
                buildCharacterAsset(
                    dtoValue = characterDTO.eyes,
                    currentAsset = baseCharacter.eyes,
                    assetType = ProfileAssetType.EYES,
                ),
            mouth =
                buildCharacterAsset(
                    dtoValue = characterDTO.mouth,
                    currentAsset = baseCharacter.mouth,
                    assetType = ProfileAssetType.MOUTH,
                ),
            home =
                buildCharacterAsset(
                    dtoValue = characterDTO.home,
                    currentAsset = baseCharacter.home,
                    assetType = ProfileAssetType.HOME,
                ),
        )
    }

    private suspend fun buildCharacterAsset(
        dtoValue: String?,
        currentAsset: CharacterAsset,
        assetType: ProfileAssetType,
    ): CharacterAsset =
        dtoValue?.let {
            characterAssetService.createCharacterAsset(it, assetType)
        } ?: currentAsset
}
