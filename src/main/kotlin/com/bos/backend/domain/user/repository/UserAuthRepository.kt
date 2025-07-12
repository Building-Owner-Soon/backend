package com.bos.backend.domain.user.repository

import com.bos.backend.domain.user.entity.UserAuth

interface UserAuthRepository {
    suspend fun save(userAuth: UserAuth): UserAuth

    suspend fun findByUserIdAndProviderType(
        userId: Long,
        providerType: String,
    ): UserAuth?

    suspend fun findByProviderIdAndProviderType(
        providerId: String,
        providerType: String,
    ): UserAuth?

    suspend fun findByEmailAndProviderType(
        email: String,
        providerType: String,
    ): UserAuth?

    suspend fun existsByEmail(email: String): Boolean

    suspend fun updateLastLoginAt(id: Long)
}
