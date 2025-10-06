package com.bos.backend.presentation.notification.controller

import com.bos.backend.application.notification.NotificationService
import com.bos.backend.presentation.notification.dto.MarkAsReadRequestDTO
import com.bos.backend.presentation.notification.dto.MarkAsReadResponseDTO
import com.bos.backend.presentation.notification.dto.NotificationResponseDTO
import com.bos.backend.presentation.notification.dto.UnreadCountResponseDTO
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/notifications")
class NotificationController(
    private val notificationService: NotificationService,
) {
    @GetMapping
    suspend fun getNotifications(
        @AuthenticationPrincipal userId: String,
        @RequestParam(defaultValue = "false") unreadOnly: Boolean,
    ): ResponseEntity<List<NotificationResponseDTO>> {
        val notifications = notificationService.getNotifications(userId.toLong(), unreadOnly)
        return ResponseEntity.ok(notifications)
    }

    @PostMapping("/mark-as-read")
    suspend fun markAsRead(
        @AuthenticationPrincipal userId: String,
        @Valid @RequestBody request: MarkAsReadRequestDTO,
    ): ResponseEntity<MarkAsReadResponseDTO> {
        val response = notificationService.markAsRead(userId.toLong(), request.notificationIds)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/unread-count")
    suspend fun getUnreadCount(
        @AuthenticationPrincipal userId: String,
    ): ResponseEntity<UnreadCountResponseDTO> {
        val unreadCount = notificationService.getUnreadCount(userId.toLong())
        return ResponseEntity.ok(UnreadCountResponseDTO(unreadCount))
    }
}
