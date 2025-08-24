package com.bos.backend.presentation.profile.dto

data class ProfileAssetResponseDTO(
    val home: List<AssetDTO>,
    val face: List<AssetDTO>,
    val hand: List<AssetDTO>,
    val bang: List<AssetDTO>,
    val backHair: List<AssetDTO>,
    val eyes: List<AssetDTO>,
    val mouth: List<AssetDTO>,
    val skinColor: List<String>,
)

data class AssetDTO(
    val key: String,
    val uri: String,
)
