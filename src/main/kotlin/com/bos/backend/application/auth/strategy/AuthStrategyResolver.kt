package com.bos.backend.application.auth.strategy

import com.bos.backend.domain.user.enum.ProviderType
import org.springframework.stereotype.Component

@Component
class AuthStrategyResolver(
    private val strategies: List<AuthStrategy>,
) {
    fun resolve(provider: ProviderType): AuthStrategy =
        strategies.find { it.providerType == provider }
            ?: throw IllegalArgumentException("Unsupported auth provider: $provider")
}
