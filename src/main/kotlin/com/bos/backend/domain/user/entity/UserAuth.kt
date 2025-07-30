package com.bos.backend.domain.user.entity

import com.bos.backend.domain.user.enum.ProviderType
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
    @Column("provider_type")
    // R2DBC doesn't have built-in AttributeConverter support like JPA
    // Manual conversion is used through getter/setter pattern
    private val _providerType: String,
    @Column("provider_id")
    val providerId: String? = null,
    @Column("email")
    val email: String,
    @Column("password_hash")
    val passwordHash: String? = null,
    @Column("last_login_at")
    val lastLoginAt: Instant? = null,
) {
    val providerType: ProviderType
        get() = ProviderType.fromValue(_providerType)

    fun updateLastLoginAt(): UserAuth = this.copy(lastLoginAt = Instant.now())

    companion object {
        fun createForBosProvider(
            userId: Long,
            email: String,
            passwordHash: String,
        ): UserAuth =
            UserAuth(
                userId = userId,
                _providerType = ProviderType.BOS.value,
                providerId = null,
                email = email,
                passwordHash = passwordHash,
            )

        fun createForKakaoProvider(
            userId: Long,
            providerId: String,
            email: String,
        ): UserAuth =
            UserAuth(
                userId = userId,
                _providerType = ProviderType.KAKAO.value,
                providerId = providerId,
                email = email,
                passwordHash = null,
            )
    }
}
