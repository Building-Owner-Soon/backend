package com.bos.backend.presentation.notification.dto

import jakarta.validation.constraints.NotEmpty

data class MarkAsReadRequestDTO(
    @field:NotEmpty(message = "알림 ID 목록은 필수입니다")
    val notificationIds: List<Long>,
)
