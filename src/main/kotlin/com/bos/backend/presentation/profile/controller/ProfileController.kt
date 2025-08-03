package com.bos.backend.presentation.profile.controller

import com.bos.backend.application.service.ProfileService
import com.bos.backend.domain.profile.enums.ProfileAssetType
import com.bos.backend.presentation.profile.dto.AssetDTO
import com.bos.backend.presentation.profile.dto.ProfileAssetResponseDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/profile")
class ProfileController(
    private val profileService: ProfileService,
) {
    @GetMapping("/assets")
    suspend fun getAssets(): ResponseEntity<ProfileAssetResponseDTO> {
        val assets = profileService.getAssets()

        val response =
            ProfileAssetResponseDTO(
                home = assets[ProfileAssetType.HOME]?.map { AssetDTO(it.key, it.uri) } ?: emptyList(),
                faceShape = assets[ProfileAssetType.FACE_SHAPE]?.map { AssetDTO(it.key, it.uri) } ?: emptyList(),
                hand = assets[ProfileAssetType.HAND]?.map { AssetDTO(it.key, it.uri) } ?: emptyList(),
                frontHair = assets[ProfileAssetType.FRONT_HAIR]?.map { AssetDTO(it.key, it.uri) } ?: emptyList(),
                backHair = assets[ProfileAssetType.BACK_HAIR]?.map { AssetDTO(it.key, it.uri) } ?: emptyList(),
                eyes = assets[ProfileAssetType.EYES]?.map { AssetDTO(it.key, it.uri) } ?: emptyList(),
                mouth = assets[ProfileAssetType.MOUTH]?.map { AssetDTO(it.key, it.uri) } ?: emptyList(),
            )

        return ResponseEntity
            .ok()
            .header("Cache-Control", "max-age=604800")
            .body(response)
    }
}
