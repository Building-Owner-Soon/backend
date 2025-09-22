package com.bos.backend.domain.notification.repository

import com.bos.backend.domain.notification.entity.Notification
import com.bos.backend.domain.notification.enums.NotificationCategory
import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface NotificationRepository {
    suspend fun save(notification: Notification): Notification

    suspend fun findById(id: Long): Notification?

    fun findByUserIdOrderByCreatedAtDesc(userId: Long): Flow<Notification>

    fun findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId: Long): Flow<Notification>

    suspend fun findAllByIdIn(ids: List<Long>): List<Notification>

    suspend fun findByUserIdAndIdIn(
        userId: Long,
        ids: List<Long>,
    ): List<Notification>

    suspend fun countByUserIdAndIsReadFalse(userId: Long): Long

    suspend fun deleteByExpiresAtBefore(expiredDate: Instant): Long

    suspend fun existsByUserIdAndCategoryAndCreatedAtAfter(
        userId: Long,
        category: NotificationCategory,
        after: Instant,
    ): Boolean
}
