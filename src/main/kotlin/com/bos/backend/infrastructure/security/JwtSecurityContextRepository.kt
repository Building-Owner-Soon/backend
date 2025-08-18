package com.bos.backend.infrastructure.security

import com.bos.backend.application.CustomException
import com.bos.backend.application.auth.AuthErrorCode
import com.bos.backend.application.service.JwtService
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtSecurityContextRepository(
    private val jwtService: JwtService,
) : ServerSecurityContextRepository {
    override fun load(exchange: ServerWebExchange): Mono<SecurityContext> {
        val token = extractToken(exchange.request) ?: return Mono.empty()

        return mono {
            validateTokenAsync(token)
        }.flatMap { securityContext ->
            securityContext?.let { Mono.just(it) } ?: Mono.empty()
        }
    }

    override fun save(
        exchange: ServerWebExchange,
        context: SecurityContext,
    ): Mono<Void> = Mono.empty()

    private suspend fun validateTokenAsync(token: String): SecurityContext? =
        runCatching {
            try {
                val isValid = jwtService.validateToken(token)
                if (!isValid) {
                    throw CustomException(AuthErrorCode.INVALID_TOKEN)
                }

                val userId = jwtService.getUserIdFromToken(token)
                val authentication = UsernamePasswordAuthenticationToken(userId.toString(), null, emptyList())
                SecurityContextImpl(authentication)
            } catch (_: Exception) {
                throw CustomException(AuthErrorCode.INVALID_TOKEN)
            }
        }.getOrNull()

    private fun extractToken(request: org.springframework.http.server.reactive.ServerHttpRequest): String? {
        val authHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        return authHeader?.takeIf { it.startsWith("Bearer ") }?.removePrefix("Bearer ")
    }
}
