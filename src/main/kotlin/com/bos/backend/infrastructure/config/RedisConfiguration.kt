package com.bos.backend.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfiguration {
    @Bean("reactiveRedisTemplate")
    @Primary
    fun reactiveRedisTemplate(
        connectionFactory: ReactiveRedisConnectionFactory,
    ): ReactiveRedisTemplate<String, String> {
        val serializer = StringRedisSerializer()
        val serializationContext =
            org.springframework.data.redis.serializer
                .RedisSerializationContext.newSerializationContext<String, String>()
                .key(serializer)
                .value(serializer)
                .hashKey(serializer)
                .hashValue(serializer)
                .build()

        return ReactiveRedisTemplate(connectionFactory, serializationContext)
    }
}
