package com.bos.backend.presentation.auth.dto

import com.bos.backend.domain.user.enum.AuthProviderType

sealed class MeResponse {
    abstract val provider: AuthProviderType
    abstract val userId: Long
    abstract val email: String
    abstract val role: String
    abstract val authDetail: AuthDetail
}

data class BosAuthMeResponse(
    override val provider: AuthProviderType = AuthProviderType.BOS,
    override val userId: Long,
    override val email: String,
    override val role: String = "USER",
    override val authDetail: BosAuthDetail,
) : MeResponse()

data class KakaoAuthMeResponse(
    override val provider: AuthProviderType = AuthProviderType.KAKAO,
    override val userId: Long,
    override val email: String,
    override val role: String = "USER",
    override val authDetail: KakaoAuthDetail,
) : MeResponse()

sealed class AuthDetail

data class BosAuthDetail(
    val isEmailVerified: Boolean,
) : AuthDetail()

data class KakaoAuthDetail(
    val providerId: String,
) : AuthDetail()
