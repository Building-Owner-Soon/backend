package com.bos.backend.domain.user.repository

import com.bos.backend.domain.user.entity.UserAuthProvider

interface UserAuthProviderRepository {
    suspend fun findById(id: Long): UserAuthProvider?

    suspend fun findByName(name: String): UserAuthProvider?

    suspend fun findAll(): List<UserAuthProvider>
}
