package com.bos.backend.presentation.profile.dto

data class RandomProfileAssetResponseDTO(
    val face: AssetDTO,
    val hand: AssetDTO,
    val bang: AssetDTO,
    val backHair: AssetDTO,
    val eyes: AssetDTO,
    val mouth: AssetDTO,
    val skinColor: String,
)
