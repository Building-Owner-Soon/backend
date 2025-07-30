package com.bos.backend.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "application.jwt")
data class JwtProperties
    @ConstructorBinding
    constructor(
        val secret: String,
        val accessTokenExpiration: Long,
        val refreshTokenExpiration: Long,
    )
