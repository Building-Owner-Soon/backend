package com.bos.backend.application.notification

import com.bos.backend.application.mapper.NotificationMapper
import com.bos.backend.domain.notification.entity.Notification
import com.bos.backend.domain.notification.enums.NotificationCategory
import com.bos.backend.domain.notification.repository.NotificationRepository
import com.bos.backend.presentation.notification.dto.MarkAsReadResponseDTO
import com.bos.backend.presentation.notification.dto.NotificationResponseDTO
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val notificationMapper: NotificationMapper,
) {
    suspend fun getNotifications(
        userId: Long,
        unreadOnly: Boolean,
    ): List<NotificationResponseDTO> {
        val notifications =
            if (unreadOnly) {
                notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)
            } else {
                notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
            }

        return notifications
            .map { notificationMapper.toNotificationResponseDTO(it) }
            .toList()
    }

    suspend fun markAsRead(
        userId: Long,
        notificationIds: List<Long>,
    ): MarkAsReadResponseDTO {
        val notifications = notificationRepository.findByUserIdAndIdIn(userId, notificationIds)

        val updatedNotifications =
            notifications.map { notification ->
                notification.markAsRead()
            }

        updatedNotifications.forEach { notification ->
            notificationRepository.save(notification)
        }

        return MarkAsReadResponseDTO(
            processedIds = notificationIds,
        )
    }

    suspend fun getUnreadCount(userId: Long): Long {
        return notificationRepository.countByUserIdAndIsReadFalse(userId)
    }

    suspend fun createNotification(
        userId: Long,
        title: String,
        content: String,
        category: NotificationCategory,
        deepLink: String?,
    ): Notification {
        val notification =
            Notification(
                userId = userId,
                title = title,
                content = content,
                category = category,
                deepLink = deepLink,
            )

        return notificationRepository.save(notification)
    }

    suspend fun deleteExpiredNotifications(): Long {
        return notificationRepository.deleteByExpiresAtBefore(Instant.now())
    }
}
