package com.bos.backend.infrastructure.util

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
class EmailHelper(
    private val mailSender: JavaMailSender,
    @Value("\${spring.mail.username}")
    private val fromEmail: String,
) {
    fun sendEmail(
        to: String,
        subject: String,
        content: String,
    ) {
        val message =
            SimpleMailMessage().apply {
                setFrom(fromEmail)
                setTo(to)
                setSubject(subject)
                setText(content)
            }
        mailSender.send(message)
    }
}
