package com.bos.backend.infrastructure.persistence

import com.bos.backend.domain.notification.entity.Notification
import com.bos.backend.domain.notification.enums.NotificationCategory
import com.bos.backend.domain.notification.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.Instant

interface NotificationCoroutineRepository : CoroutineCrudRepository<Notification, Long> {
    fun findByUserIdOrderByCreatedAtDesc(userId: Long): Flow<Notification>

    fun findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId: Long): Flow<Notification>

    suspend fun findAllByIdIn(ids: List<Long>): List<Notification>

    suspend fun findByUserIdAndIdIn(
        userId: Long,
        ids: List<Long>,
    ): List<Notification>

    suspend fun countByUserIdAndIsReadFalse(userId: Long): Long

    @Query("DELETE FROM notifications WHERE expires_at < :expiredDate")
    suspend fun deleteByExpiresAtBefore(expiredDate: Instant): Long

    suspend fun existsByUserIdAndCategoryAndCreatedAtAfter(
        userId: Long,
        category: NotificationCategory,
        after: Instant,
    ): Boolean
}

@Repository
class R2dbcNotificationRepositoryImpl(
    private val coroutineRepository: NotificationCoroutineRepository,
) : NotificationRepository {
    override suspend fun save(notification: Notification): Notification = coroutineRepository.save(notification)

    override suspend fun findById(id: Long): Notification? = coroutineRepository.findById(id)

    override fun findByUserIdOrderByCreatedAtDesc(userId: Long): Flow<Notification> =
        coroutineRepository.findByUserIdOrderByCreatedAtDesc(userId)

    override fun findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId: Long): Flow<Notification> =
        coroutineRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)

    override suspend fun findAllByIdIn(ids: List<Long>): List<Notification> = coroutineRepository.findAllByIdIn(ids)

    override suspend fun findByUserIdAndIdIn(
        userId: Long,
        ids: List<Long>,
    ): List<Notification> = coroutineRepository.findByUserIdAndIdIn(userId, ids)

    override suspend fun countByUserIdAndIsReadFalse(userId: Long): Long =
        coroutineRepository.countByUserIdAndIsReadFalse(userId)

    override suspend fun deleteByExpiresAtBefore(expiredDate: Instant): Long =
        coroutineRepository.deleteByExpiresAtBefore(expiredDate)

    override suspend fun existsByUserIdAndCategoryAndCreatedAtAfter(
        userId: Long,
        category: NotificationCategory,
        after: Instant,
    ): Boolean = coroutineRepository.existsByUserIdAndCategoryAndCreatedAtAfter(userId, category, after)
}
