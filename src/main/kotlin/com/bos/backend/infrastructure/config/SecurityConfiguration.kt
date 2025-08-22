package com.bos.backend.infrastructure.config

import com.bos.backend.infrastructure.security.JwtSecurityContextRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
class SecurityConfiguration(
    private val jwtSecurityContextRepository: JwtSecurityContextRepository,
) {
    @Bean
    fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .logout { it.disable() }
            .authorizeExchange {
                it
                    .pathMatchers(
                        "/auth/sign-up",
                        "/auth/sign-in",
                        "/auth/email-verification/**",
                        "/auth/password-reset",
                        "/auth/check-email",
                        "/api/**",
                        "/actuator/**",
                        "/admin/**",
                    ).permitAll()
                    .anyExchange()
                    .authenticated()
            }.securityContextRepository(jwtSecurityContextRepository)
            .build()
}
