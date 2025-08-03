package com.bos.backend.application.service

import com.bos.backend.domain.profile.enums.ProfileAssetType
import com.bos.backend.infrastructure.external.AssetInfo
import com.bos.backend.infrastructure.external.AssetService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service

@Service
class ProfileService(
    private val assetService: AssetService,
) {
    suspend fun getAssets(): Map<ProfileAssetType, List<AssetInfo>> =
        coroutineScope {
            ProfileAssetType.entries
                .map { type ->
                    async {
                        val prefix = "profile/${type.name.lowercase().replace('_', '-')}/"
                        val assets = assetService.getAssetsByPrefix(prefix)
                        type to assets
                    }
                }.awaitAll()
                .toMap()
        }
}
