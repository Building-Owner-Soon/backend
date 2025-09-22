package com.bos.backend.domain.notification.entity

import com.bos.backend.domain.notification.enums.NotificationCategory
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("notifications")
data class Notification(
    @Id
    val id: Long? = null,
    @Column("user_id")
    val userId: Long,
    val title: String,
    val content: String,
    val category: NotificationCategory,
    @Column("deep_link")
    val deepLink: String? = null,
    @Column("is_read")
    val isRead: Boolean = false,
    @Column("read_at")
    val readAt: Instant? = null,
    @Column("created_at")
    val createdAt: Instant = Instant.now(),
    @Column("expires_at")
    @Suppress("MagicNumber")
    val expiresAt: Instant = Instant.now().plusSeconds(90 * 24 * 60 * 60),
) {
    fun markAsRead(): Notification {
        return if (!isRead) {
            this.copy(
                isRead = true,
                readAt = Instant.now(),
            )
        } else {
            this
        }
    }

    fun isExpired(): Boolean {
        return Instant.now().isAfter(expiresAt)
    }
}
