package com.bos.backend.infrastructure.persistence

import com.bos.backend.domain.user.entity.UserAuth
import com.bos.backend.domain.user.repository.UserAuthRepository
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

interface UserAuthCoroutineRepository : CoroutineCrudRepository<UserAuth, Long> {
    @Query("SELECT * FROM user_auth WHERE provider_id = :providerId AND provider_type = :providerType")
    suspend fun findByProviderIdAndProviderType(
        @Param("providerId") providerId: String,
        @Param("providerType") providerType: String,
    ): UserAuth?

    @Query("SELECT * FROM user_auth WHERE email = :email AND provider_type = :providerType")
    suspend fun findByEmailAndProviderType(
        @Param("email") email: String,
        @Param("providerType") providerType: String,
    ): UserAuth?

    @Query("SELECT COUNT(*) > 0 FROM user_auth WHERE email = :email")
    suspend fun existsByEmail(
        @Param("email") email: String,
    ): Boolean

    @Modifying
    @Query("UPDATE user_auth SET last_login_at = NOW() WHERE id = :id")
    suspend fun updateLastLoginAt(
        @Param("id") id: Long,
    ): Int
}

@Repository
class R2dbcUserAuthRepositoryImpl(
    private val coroutineRepository: UserAuthCoroutineRepository,
) : UserAuthRepository {
    override suspend fun save(userAuth: UserAuth): UserAuth = coroutineRepository.save(userAuth)

    override suspend fun findByProviderIdAndProviderType(
        providerId: String,
        providerType: String,
    ): UserAuth? = coroutineRepository.findByProviderIdAndProviderType(providerId, providerType)

    override suspend fun findByEmailAndProviderType(
        email: String,
        providerType: String,
    ): UserAuth? = coroutineRepository.findByEmailAndProviderType(email, providerType)

    override suspend fun existsByEmail(email: String): Boolean = coroutineRepository.existsByEmail(email)

    override suspend fun updateLastLoginAt(id: Long) {
        coroutineRepository.updateLastLoginAt(id)
    }
}
