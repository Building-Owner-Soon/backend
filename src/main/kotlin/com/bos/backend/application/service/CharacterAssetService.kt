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
        assetId: String,
        assetType: ProfileAssetType,
    ): CharacterAsset {
        val assetList = getAssetsByType(assetType)

        // FACE_TYPE_2 형태의 assetId를 받아서 S3에서 조회할 때 사용할 키로 변환
        val searchKey = assetId.uppercase()
        val assetInfo = assetList.find { it.id == searchKey }

        val uri =
            assetInfo?.uri ?: run {
                // fallback: FACE_TYPE_2 → face-type-2로 변환해서 S3 경로 생성
                val fileName = assetId.lowercase().replace('_', '-')
                val typePrefix = assetType.name.lowercase().replace('_', '-')
                "https://bos-assets.s3.ap-northeast-2.amazonaws.com/profile/$typePrefix/$fileName.svg"
            }

        return CharacterAsset(id = assetId, uri = URI.create(uri))
    }
}
