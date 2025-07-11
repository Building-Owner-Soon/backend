package com.bos.backend.domain.user.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("user_auths")
data class UserAuth(
    @Id
    val id: Long? = null,
    @Column("user_id")
    val userId: Long,
    @Column("user_provider_id")
    val userProviderId: Long,
    @Column("provider_id")
    val providerId: String,
    @Column("password_hash")
    val passwordHash: String? = null,
    @Column("last_login_at")
    val lastLoginAt: Instant? = null,
) {
    fun updateLastLoginAt(): UserAuth = this.copy(lastLoginAt = Instant.now())
}
