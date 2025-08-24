package com.bos.backend.application.service

import com.bos.backend.domain.profile.enums.ProfileAssetType
import com.bos.backend.domain.user.entity.CharacterAsset
import com.bos.backend.infrastructure.external.AssetInfo
import org.springframework.stereotype.Service
import java.net.URI

@Service
class CharacterAssetService(
    private val profileService: ProfileService,
) {
    suspend fun getAssetsByType(assetType: ProfileAssetType): List<AssetInfo> {
        val assets = profileService.getAssets()
        return assets[assetType] ?: emptyList()
    }

    suspend fun createCharacterAsset(
        id: Int,
        assetType: ProfileAssetType,
    ): CharacterAsset {
        val assetList = getAssetsByType(assetType)

        val assetKey = "${assetType.name}_$id"
        val assetInfo =
            assetList.find { it.key == assetKey }
                ?: assetList.find { it.key.endsWith("_$id") }

        val assetId = assetInfo?.key ?: "${assetType.name.lowercase()}_$id"
        val uri =
            assetInfo?.uri ?: run {
                // TODO: fallback
                val typePrefix = assetType.name.lowercase().replace('_', '-')
                "https://bos-assets.s3.ap-northeast-2.amazonaws.com/profile/$typePrefix/$id.svg"
            }

        return CharacterAsset(id = assetId, uri = URI.create(uri))
    }
}
