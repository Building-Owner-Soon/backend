package com.bos.backend.infrastructure

import com.bos.backend.application.service.JwtService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.Instant
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtServiceImpl(
    // TODO: ApplicationProperties로 env 관리
    @Value("\${application.jwt.secret}") private val secret: String,
) : JwtService {
    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    companion object {
        private const val JWT_PART_COUNT = 3
    }

    override fun generateToken(
        userId: String,
        expirationSecond: Long,
    ): String {
        val currentTime = Instant.now()
        val expDate = Date.from(currentTime.plusSeconds(expirationSecond))

        return Jwts
            .builder()
            .subject(userId)
            .issuedAt(Date.from(currentTime))
            .expiration(expDate)
            .signWith(key)
            .compact()
    }

    override fun validateToken(token: String): Boolean = runCatching { parseClaimFromToken(token) }.isSuccess

    override fun getUserIdFromToken(token: String): Long = parseClaimFromToken(token).payload.subject.toLong()

    override fun hashToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(token.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }

    override fun validateTokenFormat(token: String): Boolean =
        runCatching {
            token.split(".").size == JWT_PART_COUNT &&
                token.matches(Regex("^[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+$"))
        }.getOrDefault(false)

    private fun parseClaimFromToken(token: String): Jws<Claims> =
        Jwts
            .parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
}
