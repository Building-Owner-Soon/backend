package com.bos.backend.presentation.profile.controller

import com.bos.backend.application.service.ProfileService
import com.bos.backend.domain.profile.constants.SKIN_COLORS
import com.bos.backend.domain.profile.enums.ProfileAssetType
import com.bos.backend.presentation.profile.dto.AssetDTO
import com.bos.backend.presentation.profile.dto.ProfileAssetResponseDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/profile")
class ProfileController(
    private val profileService: ProfileService,
) {
    @GetMapping("/assets")
    suspend fun getAssets(
        @RequestHeader(value = "If-None-Match", required = false) ifNoneMatch: String?,
    ): ResponseEntity<ProfileAssetResponseDTO> {
        val currentETag = profileService.getCurrentETag()

        if (ifNoneMatch != null && ifNoneMatch == currentETag) {
            return ResponseEntity
                .status(HttpStatus.NOT_MODIFIED)
                .header("ETag", currentETag)
                .header("Cache-Control", "max-age=86400, must-revalidate")
                .build()
        }

        val assets = profileService.getAssets()
        val response =
            ProfileAssetResponseDTO(
                home = assets[ProfileAssetType.HOME]?.map { AssetDTO(it.key, it.uri) } ?: emptyList(),
                face = assets[ProfileAssetType.FACE]?.map { AssetDTO(it.key, it.uri) } ?: emptyList(),
                hand = assets[ProfileAssetType.HAND]?.map { AssetDTO(it.key, it.uri) } ?: emptyList(),
                bang = assets[ProfileAssetType.BANG]?.map { AssetDTO(it.key, it.uri) } ?: emptyList(),
                backHair = assets[ProfileAssetType.BACK_HAIR]?.map { AssetDTO(it.key, it.uri) } ?: emptyList(),
                eyes = assets[ProfileAssetType.EYES]?.map { AssetDTO(it.key, it.uri) } ?: emptyList(),
                mouth = assets[ProfileAssetType.MOUTH]?.map { AssetDTO(it.key, it.uri) } ?: emptyList(),
                skinColor = SKIN_COLORS,
            )

        val etag = "\"profile-v${System.currentTimeMillis()}\""

        return ResponseEntity
            .ok()
            .header("ETag", etag)
            .header("Cache-Control", "max-age=86400, must-revalidate")
            .body(response)
    }
}
