package club.memoni.backend.poc.ai

import reactor.core.publisher.Mono

interface LLMService {
    fun analyze(test: String): Mono<String>
}
