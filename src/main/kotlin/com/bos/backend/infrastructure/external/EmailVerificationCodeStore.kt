package com.bos.backend.infrastructure.external

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class EmailVerificationCodeStore(
    @Qualifier("reactiveRedisTemplate")
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
) {
    companion object {
        private const val VERIFICATION_CODE_TTL_MINUTES = 10
    }

    suspend fun saveVerificationCode(
        email: String,
        code: String,
    ) {
        val key = "email:verification:$email"
        redisTemplate
            .opsForValue()
            .set(key, code, Duration.ofMinutes(VERIFICATION_CODE_TTL_MINUTES.toLong()))
            .awaitSingle()
    }

    suspend fun getVerificationCode(email: String): String? {
        val key = "email:verification:$email"
        return redisTemplate
            .opsForValue()
            .get(key)
            .awaitSingleOrNull()
    }

    suspend fun deleteVerificationCode(email: String) {
        val key = "email:verification:$email"
        redisTemplate.delete(key).awaitSingle()
    }
}
