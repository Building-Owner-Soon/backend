package com.bos.backend.presentation.user.dto

data class UpdateUserRequestDTO(
    val nickname: String? = null,
    val character: UpdateCharacterDTO? = null,
    val homeType: String? = null,
    val isNotificationAllowed: Boolean? = null,
    val isMarketingAgreed: Boolean? = null,
)

data class UpdateCharacterDTO(
    val faceShape: Int,
    val hand: Int,
    val skinColor: String,
    val frontHair: Int,
    val backHair: Int,
    val eyes: Int,
    val mouth: Int,
)
