package com.bos.backend.presentation.user.dto

import com.bos.backend.domain.user.entity.CharacterComponents
import java.time.Instant

data class UserProfileDTO(
    val id: Long,
    val nickname: String,
    val characterComponents: CharacterComponents? = null,
    val homeType: String? = null,
    val isNotificationAllowed: Boolean,
    val isMarketingAgreed: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
)
