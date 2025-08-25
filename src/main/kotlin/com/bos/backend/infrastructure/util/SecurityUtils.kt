package com.bos.backend.infrastructure.util

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.security.core.context.ReactiveSecurityContextHolder

object SecurityUtils {
    suspend fun getCurrentUserId(): Long {
        val authentication =
            ReactiveSecurityContextHolder.getContext()
                .awaitSingle()
                .authentication

        return authentication.name.toLong()
    }
}
