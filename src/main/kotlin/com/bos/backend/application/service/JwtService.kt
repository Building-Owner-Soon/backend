package com.bos.backend.application.service

interface JwtService {
    fun generateToken(
        userId: String,
        expirationSecond: Long,
    ): String

    fun validateToken(token: String): Boolean

    fun getUserIdFromToken(token: String): Long

    fun hashToken(token: String): String

    fun validateTokenFormat(token: String): Boolean
}
