package com.bos.backend.infrastructure.external

import kotlinx.coroutines.future.await
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.time.Duration

@Service
class AssetService(
    private val s3AsyncClient: S3AsyncClient,
    private val s3Presigner: S3Presigner,
    @Value("\${app.s3.bucket-name}") private val bucketName: String,
) {
    suspend fun getAssetsByPrefix(prefix: String): List<AssetInfo> {
        val request =
            ListObjectsV2Request
                .builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build()

        val response = s3AsyncClient.listObjectsV2(request).await()

        return response
            .contents()
            .filter { !it.key().endsWith("/") }
            .map { obj ->
                val fileName = obj.key().substringAfterLast("/")
                val key = fileName.substringBeforeLast(".").uppercase().replace('-', '_')
                val uri = generatePresignedUrl(obj.key())

                AssetInfo(id = key, uri = uri)
            }
    }

    private fun generatePresignedUrl(key: String): String {
        val getObjectRequest =
            GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build()

        val presignRequest =
            GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(1))
                .getObjectRequest(getObjectRequest)
                .build()

        return s3Presigner.presignGetObject(presignRequest).url().toString()
    }
}

data class AssetInfo(
    val id: String,
    val uri: String,
)
