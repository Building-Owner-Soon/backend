package com.bos.backend.infrastructure.listener

import com.bos.backend.infrastructure.event.EmailVerificationEvent
import com.bos.backend.infrastructure.util.EmailHelper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.event.EventListener
import org.springframework.mail.MailException
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component
import java.util.concurrent.RejectedExecutionException

@Component
class EmailVerificationEventListener(
    private val emailHelper: EmailHelper,
    @Qualifier("emailTaskExecutor")
    private val emailTaskExecutor: ThreadPoolTaskExecutor,
) {
    private val logger = LoggerFactory.getLogger(EmailVerificationEventListener::class.java)

    companion object {
        private const val QUEUE_USAGE_SCALE_UP_THRESHOLD = 80
    }

    @EventListener
    @Async
    fun handleEmailVerificationEvent(event: EmailVerificationEvent) {
        val queueSize = emailTaskExecutor.queueSize
        val queueCapacity = emailTaskExecutor.queueCapacity
        val usagePercentage = (queueSize * 100 / queueCapacity)
        
        if (usagePercentage >= QUEUE_USAGE_SCALE_UP_THRESHOLD) {
            logger.warn("Email queue usage is high: $queueSize/$queueCapacity ($usagePercentage%)")
            val currentPoolSize = emailTaskExecutor.corePoolSize
            val maxPoolSize = emailTaskExecutor.maxPoolSize
            if (currentPoolSize < maxPoolSize) {
                emailTaskExecutor.setCorePoolSize(currentPoolSize + 1)
                logger.info("Auto scaled email thread pool: $currentPoolSize → ${currentPoolSize + 1}")
            }
        }
        try {
            logger.info("Sending verification email to: ${event.email}")
            emailHelper.sendEmail(
                to = event.email,
                subject = event.subject,
                content = event.content,
            )
            logger.info("Verification email sent successfully to: ${event.email}")
        } catch (_: RejectedExecutionException) {
            logger.error("Email queue/pool full. Email dropped: ${event.email}")
        } catch (e: MailException) {
            logger.error("Mail sending failed: ${event.email}. Error: ${e.message}")
        }
    }
}
