package com.bos.backend.application.mapper

import com.bos.backend.domain.user.entity.Character
import com.bos.backend.domain.user.entity.CharacterAsset
import org.springframework.stereotype.Component

data class CharacterAssets(
    val faceShape: CharacterAsset,
    val hand: CharacterAsset,
    val frontHair: CharacterAsset,
    val backHair: CharacterAsset,
    val eyes: CharacterAsset,
    val mouth: CharacterAsset,
)

@Component
class CharacterMapper {
    fun toCharacter(
        assets: CharacterAssets,
        skinColor: String,
    ): Character =
        Character(
            face = assets.faceShape,
            hand = assets.hand,
            skinColor = skinColor,
            bang = assets.frontHair,
            backHair = assets.backHair,
            eyes = assets.eyes,
            mouth = assets.mouth,
        )
}
