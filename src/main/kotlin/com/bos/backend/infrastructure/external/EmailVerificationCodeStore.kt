package com.bos.backend.infrastructure.external

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class EmailVerificationCodeStore(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    companion object {
        private const val VERIFICATION_CODE_TTL_MINUTES = 10
    }

    suspend fun saveVerificationCode(
        email: String,
        code: String,
        purpose: String,
    ) {
        val key = "email:verification:$purpose:$email"
        redisTemplate.opsForValue().set(key, code, Duration.ofMinutes(VERIFICATION_CODE_TTL_MINUTES.toLong()))
    }

    suspend fun getVerificationCode(
        email: String,
        purpose: String,
    ): String? {
        val key = "email:verification:$purpose:$email"
        return redisTemplate.opsForValue().get(key)
    }

    suspend fun deleteVerificationCode(
        email: String,
        purpose: String,
    ) {
        val key = "email:verification:$purpose:$email"
        redisTemplate.delete(key)
    }
}
