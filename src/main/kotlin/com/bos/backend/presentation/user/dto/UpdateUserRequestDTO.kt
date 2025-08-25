package com.bos.backend.presentation.user.dto

data class UpdateUserRequestDTO(
    val nickname: String? = null,
    val character: UpdateCharacterDTO? = null,
    val homeType: String? = null,
    val isNotificationAllowed: Boolean? = null,
    val isMarketingAgreed: Boolean? = null,
)

data class UpdateCharacterDTO(
    val face: String? = null,
    val hand: String? = null,
    val skinColor: String? = null,
    val bang: String? = null,
    val backHair: String? = null,
    val eyes: String? = null,
    val mouth: String? = null,
)
