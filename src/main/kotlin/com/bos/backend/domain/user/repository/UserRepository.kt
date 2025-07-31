package com.bos.backend.domain.user.repository

import com.bos.backend.domain.user.entity.User

interface UserRepository {
    suspend fun save(user: User): User

    suspend fun findById(id: Long): User?

    suspend fun deleteById(id: Long)
}
