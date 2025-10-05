package com.bos.backend.presentation.profile.controller

import com.bos.backend.application.service.ProfileService
import com.bos.backend.domain.profile.enums.ProfileAssetType
import com.bos.backend.presentation.profile.dto.AssetDTO
import com.bos.backend.presentation.profile.dto.ProfileAssetResponseDTO
import com.bos.backend.presentation.profile.dto.RandomProfileAssetResponseDTO
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

        val (assets, etag) = profileService.getAssetsWithETag()
        val response =
            ProfileAssetResponseDTO(
                home = assets[ProfileAssetType.HOME]?.map { AssetDTO(it.id, it.uri) } ?: emptyList(),
                face = assets[ProfileAssetType.FACE]?.map { AssetDTO(it.id, it.uri) } ?: emptyList(),
                hand = assets[ProfileAssetType.HAND]?.map { AssetDTO(it.id, it.uri) } ?: emptyList(),
                bang = assets[ProfileAssetType.BANG]?.map { AssetDTO(it.id, it.uri) } ?: emptyList(),
                backHair = assets[ProfileAssetType.BACK_HAIR]?.map { AssetDTO(it.id, it.uri) } ?: emptyList(),
                eyes = assets[ProfileAssetType.EYES]?.map { AssetDTO(it.id, it.uri) } ?: emptyList(),
                mouth = assets[ProfileAssetType.MOUTH]?.map { AssetDTO(it.id, it.uri) } ?: emptyList(),
                skinColor = profileService.getSkinColors(),
            )

        return ResponseEntity
            .ok()
            .header("ETag", etag)
            .header("Cache-Control", "max-age=86400, must-revalidate")
            .body(response)
    }

    @GetMapping("/assets/random")
    suspend fun getRandomAssets(): ResponseEntity<RandomProfileAssetResponseDTO> {
        val randomAssets = profileService.getRandomAsset()
        val response =
            RandomProfileAssetResponseDTO(
                home = randomAssets[ProfileAssetType.HOME]!!.let { AssetDTO(it.id, it.uri) },
                face = randomAssets[ProfileAssetType.FACE]!!.let { AssetDTO(it.id, it.uri) },
                hand = randomAssets[ProfileAssetType.HAND]!!.let { AssetDTO(it.id, it.uri) },
                bang = randomAssets[ProfileAssetType.BANG]!!.let { AssetDTO(it.id, it.uri) },
                backHair = randomAssets[ProfileAssetType.BACK_HAIR]!!.let { AssetDTO(it.id, it.uri) },
                eyes = randomAssets[ProfileAssetType.EYES]!!.let { AssetDTO(it.id, it.uri) },
                mouth = randomAssets[ProfileAssetType.MOUTH]!!.let { AssetDTO(it.id, it.uri) },
                skinColor = "#FEEFE7",
            )

        return ResponseEntity.ok(response)
    }
}
