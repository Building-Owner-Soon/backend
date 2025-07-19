package com.bos.backend.infrastructure

import com.bos.backend.application.service.JwtService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtServiceImpl(
    // TODO: ApplicationProperties로 env 관리
    @Value("\${application.jwt.secret}") private val secret: String,
) : JwtService {
    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

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

    private fun parseClaimFromToken(token: String): Jws<Claims> =
        Jwts
            .parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
}
