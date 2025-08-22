package com.bos.backend.infrastructure.external

import kotlinx.coroutines.future.await
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request

@Service
class AssetService(
    private val s3AsyncClient: S3AsyncClient,
    @Value("\${app.s3.bucket-name}") private val bucketName: String,
    @Value("\${app.s3.base-url}") private val baseUrl: String,
) {
    suspend fun getAssetsByPrefix(prefix: String): List<AssetInfo> {
        val request =
            ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build()

        val response = s3AsyncClient.listObjectsV2(request).await()

        return response.contents()
            .filter { !it.key().endsWith("/") }
            .map { obj ->
                val fileName = obj.key().substringAfterLast("/")
                val key = fileName.substringBeforeLast(".").uppercase().replace('-', '_')
                val uri = "$baseUrl/${obj.key()}"

                AssetInfo(key = key, uri = uri)
            }
    }
}

data class AssetInfo(
    val key: String,
    val uri: String,
)
