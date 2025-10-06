package com.bos.backend.presentation.user.dto

import com.bos.backend.domain.user.entity.Character
import java.time.Instant

data class UpdatedUserProfileResponseDTO(
    val id: Long,
    val nickname: String,
    val character: Character? = null,
    val isNotificationAllowed: Boolean,
    val isMarketingAgreed: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
)
