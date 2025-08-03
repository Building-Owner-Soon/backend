package com.bos.backend.presentation.profile.dto

data class ProfileAssetResponseDTO(
    val home: List<AssetDTO>,
    val faceShape: List<AssetDTO>,
    val hand: List<AssetDTO>,
    val frontHair: List<AssetDTO>,
    val backHair: List<AssetDTO>,
    val eyes: List<AssetDTO>,
    val mouth: List<AssetDTO>,
)

data class AssetDTO(
    val key: String,
    val uri: String,
)
