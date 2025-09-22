package com.bos.backend.infrastructure.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
@EnableAsync
@EnableScheduling
class AsyncConfiguration {
    companion object {
        private const val CORE_POOL_SIZE = 2
        private const val MAX_POOL_SIZE = 5
        private const val QUEUE_CAPACITY = 100
    }

    @Bean("emailTaskExecutor")
    fun emailTaskExecutor(): ThreadPoolTaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = CORE_POOL_SIZE
        executor.maxPoolSize = MAX_POOL_SIZE
        executor.queueCapacity = QUEUE_CAPACITY
        executor.setThreadNamePrefix("email-")
        executor.initialize()
        return executor
    }
}
