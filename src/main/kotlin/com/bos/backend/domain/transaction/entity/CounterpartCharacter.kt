package com.bos.backend.domain.transaction.entity

import com.bos.backend.domain.user.entity.CharacterAsset

data class CounterpartCharacter(
    val face: CharacterAsset,
    val hand: CharacterAsset,
    val skinColor: String,
    val bang: CharacterAsset,
    val backHair: CharacterAsset,
    val eyes: CharacterAsset,
    val mouth: CharacterAsset,
)
