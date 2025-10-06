package com.bos.backend.application.service

import com.bos.backend.domain.profile.constants.SKIN_COLORS
import com.bos.backend.domain.profile.enums.ProfileAssetType
import com.bos.backend.infrastructure.external.AssetInfo
import com.bos.backend.infrastructure.external.AssetService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
@Suppress("TooManyFunctions")
class ProfileService(
    private val assetService: AssetService,
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {
    private val logger = LoggerFactory.getLogger(ProfileService::class.java)

    companion object {
        private const val CACHE_KEY = "profile:assets:all"
        private const val ETAG_KEY = "profile:assets:etag"
        private val CACHE_TTL = Duration.ofHours(24)
    }

    suspend fun getAssets(): Map<ProfileAssetType, List<AssetInfo>> {
        val cachedResult = getCachedAssets()
        if (cachedResult != null) {
            return cachedResult
        }

        val assets = loadAssetsFromS3()
        setCachedAssets(assets)
        return assets
    }

    suspend fun getAssetsWithETag(): Pair<Map<ProfileAssetType, List<AssetInfo>>, String> {
        val cachedResult = getCachedAssets()
        val currentETag = getCurrentETag()

        if (cachedResult != null && currentETag != null) {
            return cachedResult to currentETag
        }

        val assets = loadAssetsFromS3()
        val newETag = setCachedAssetsAndReturnETag(assets)
        return assets to newETag
    }

    suspend fun getCurrentETag(): String? = redisTemplate.opsForValue().get(ETAG_KEY)

    suspend fun getSkinColors(): List<String> = SKIN_COLORS

    suspend fun getRandomAsset(): Map<ProfileAssetType, AssetInfo> {
        val assets = getAssets()
        return ProfileAssetType.entries.associateWith { type ->
            val assetList = assets[type] ?: emptyList()
            checkNotNull(assetList.randomOrNull())
        }
    }

    suspend fun evictCache() {
        redisTemplate.delete(CACHE_KEY)
        redisTemplate.delete(ETAG_KEY)
    }

    @Suppress("TooGenericExceptionCaught")
    suspend fun getCacheStatus(): Map<String, Any> =
        try {
            val cacheExists = redisTemplate.hasKey(CACHE_KEY)
            val ttl = if (cacheExists) redisTemplate.getExpire(CACHE_KEY) else -1L

            mapOf(
                "cacheExists" to cacheExists,
                "ttlSeconds" to ttl,
                "cacheKey" to CACHE_KEY,
                "maxTtlHours" to CACHE_TTL.toHours(),
            )
        } catch (e: Exception) {
            logger.error("Failed to get cache status", e)
            mapOf(
                "error" to "Failed to retrieve cache status",
                "cacheKey" to CACHE_KEY,
            )
        }

    private fun getCachedAssets(): Map<ProfileAssetType, List<AssetInfo>>? =
        try {
            val cachedJson = redisTemplate.opsForValue().get(CACHE_KEY)
            if (cachedJson != null) {
                objectMapper
                    .readValue<Map<String, List<AssetInfo>>>(cachedJson)
                    .mapKeys { ProfileAssetType.valueOf(it.key) }
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }

    @Suppress("TooGenericExceptionCaught")
    private fun setCachedAssets(assets: Map<ProfileAssetType, List<AssetInfo>>) {
        setCachedAssetsAndReturnETag(assets)
    }

    @Suppress("TooGenericExceptionCaught")
    private fun setCachedAssetsAndReturnETag(assets: Map<ProfileAssetType, List<AssetInfo>>): String {
        val etag = "\"profile-v${System.currentTimeMillis()}\""
        return try {
            val jsonString =
                objectMapper.writeValueAsString(
                    assets.mapKeys { it.key.name },
                )

            val operations = redisTemplate.opsForValue()
            operations.set(CACHE_KEY, jsonString, CACHE_TTL)
            operations.set(ETAG_KEY, etag, CACHE_TTL)

            etag
        } catch (e: Exception) {
            logger.error("Failed to cache assets in Redis", e)
            etag
        }
    }

    private suspend fun loadAssetsFromS3(): Map<ProfileAssetType, List<AssetInfo>> =
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
