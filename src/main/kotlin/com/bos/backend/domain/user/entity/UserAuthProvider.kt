package com.bos.backend.domain.user.entity

import com.bos.backend.domain.user.enum.AuthProviderType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("user_auth_providers")
data class UserAuthProvider(
    @Id
    val id: Long? = null,
    val name: String,
) {
    fun toProviderType(): AuthProviderType = AuthProviderType.fromValue(name)
}
