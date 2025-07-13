package com.bos.backend.application.auth.strategy

import com.bos.backend.domain.user.enum.ProviderType
import org.springframework.stereotype.Component

@Component
class AuthStrategyResolver(
    private val strategies: List<AuthStrategy>,
) {
    fun resolve(provider: String): AuthStrategy {
        val providerType = ProviderType.fromValue(provider)
        return strategies.find { it.providerType == providerType }
            ?: throw IllegalArgumentException("Unsupported auth provider: $provider")
    }
}
