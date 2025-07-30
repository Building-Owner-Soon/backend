package com.bos.backend.presentation.user.dto

import com.bos.backend.domain.user.entity.CharacterComponents

data class UpdateUserRequestDTO(
    val nickname: String? = null,
    val characterComponents: CharacterComponents? = null,
    val homeType: String? = null,
    val isNotificationAllowed: Boolean? = null,
    val isMarketingAgreed: Boolean? = null,
)
