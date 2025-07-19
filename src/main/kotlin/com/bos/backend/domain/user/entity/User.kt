package com.bos.backend.domain.user.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("users")
data class User(
    @Id
    val id: Long? = null,
    val nickname: String? = null,
    @Column("profile_image_url")
    val profileImageUrl: String? = null,
    @Column("allow_notification")
    val allowNotification: Boolean = true,
    @Column("created_at")
    val createdAt: Instant = Instant.now(),
    @Column("updated_at")
    val updatedAt: Instant = Instant.now(),
    @Column("deleted_at")
    val deletedAt: Instant? = null,
) {
    fun isDeleted(): Boolean = deletedAt != null

    fun delete(): User = this.copy(deletedAt = Instant.now(), updatedAt = Instant.now())
}
