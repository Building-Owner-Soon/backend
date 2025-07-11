package com.bos.backend.domain.user.repository

import com.bos.backend.domain.user.entity.UserAuth

interface UserAuthRepository {
    suspend fun save(userAuth: UserAuth): UserAuth

    suspend fun findById(id: Long): UserAuth?

    suspend fun findByProviderIdAnd(
        userId: Long,
        providerId: Long,
    ): UserAuth?

    suspend fun findByProviderIdAndProviderId(
        providerId: String,
        userProviderId: Long,
    ): UserAuth?
}
