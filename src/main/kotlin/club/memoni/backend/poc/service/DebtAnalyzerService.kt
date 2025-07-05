package club.memoni.backend.poc.service

import club.memoni.backend.poc.ai.LLMService
import club.memoni.backend.poc.dto.DebtInfo
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class DebtAnalyzerService(
    private val llmService: LLMService,
    private val objectMapper: ObjectMapper
) {
    fun analyze(text: String): Mono<DebtInfo> {
        return llmService.analyze(text)
            .map { jsonResponse ->
                try {
                    objectMapper.readValue(jsonResponse, DebtInfo::class.java)
                } catch (e: Exception) {
                    throw IllegalStateException("Failed to parse LLM response: $jsonResponse", e)
                }
            }
    }
}
