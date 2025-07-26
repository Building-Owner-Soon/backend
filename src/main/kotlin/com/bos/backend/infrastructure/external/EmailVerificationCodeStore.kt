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
    ) {
        val key = "email:verification:$email"
        redisTemplate.opsForValue().set(key, code, Duration.ofMinutes(VERIFICATION_CODE_TTL_MINUTES.toLong()))
    }

    suspend fun getVerificationCode(email: String): String? {
        val key = "email:verification:$email"
        return redisTemplate.opsForValue().get(key)
    }

    suspend fun deleteVerificationCode(email: String) {
        val key = "email:verification:$email"
        redisTemplate.delete(key)
    }
}
