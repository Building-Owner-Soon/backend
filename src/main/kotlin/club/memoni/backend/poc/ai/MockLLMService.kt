package club.memoni.backend.poc.ai

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
@Profile("test")
class MockLLMService : LLMService {
    override fun analyze(text: String): Mono<String> {
        val currentDate = LocalDate.of(2025, 6, 1)

        return Mono.just(
            when {
                // 일시불 케이스
                text.contains("지영") && text.contains("5만원") -> """
                    {
                      "creditor": "지영",
                      "type": "SINGLE",
                      "debtor": "나",
                      "amount": 50000,
                      "dueDate": "2025-06-15",
                      "reason": "생일 선물"
                    }
                """.trimIndent()

                // 분할 상환 케이스
                text.contains("홍길동") && text.contains("50만원") -> """
                    {
                      "creditor": "홍길동",
                      "type": "INSTALLMENT",
                      "debtor": "나",
                      "amount": 500000,
                      "dueDate": null,
                      "reason": null,
                      "installment": {
                        "startDate": "2025-07-05",
                        "installmentAmount": 100000,
                        "installmentCycle": "MONTHLY",
                        "installmentDay": 5,
                        "totalInstallments": 5
                      }
                    }
                """.trimIndent()

                // 이유가 포함된 케이스
                text.contains("철수") && text.contains("병원비") -> """
                    {
                      "creditor": "철수",
                      "type": "SINGLE",
                      "debtor": "나",
                      "amount": 300000,
                      "dueDate": "${currentDate.plusDays(7).format(DateTimeFormatter.ISO_DATE)}",
                      "reason": "급한 병원비"
                    }
                """.trimIndent()

                // 복잡한 분할 상환 케이스
                text.contains("회사") && text.contains("200만원") -> """
                    {
                      "creditor": "회사",
                      "type": "INSTALLMENT",
                      "debtor": "나",
                      "amount": 2000000,
                      "dueDate": null,
                      "reason": null,
                      "installment": {
                        "startDate": "2025-07-15",
                        "installmentAmount": 700000,
                        "installmentCycle": "MONTHLY",
                        "installmentDay": 15,
                        "totalInstallments": 3
                      }
                    }
                """.trimIndent()

                // 기본 응답
                else -> """
                    {
                      "creditor": "알 수 없음",
                      "type": "SINGLE",
                      "debtor": "나",
                      "amount": 0,
                      "dueDate": null,
                      "reason": null
                    }
                """.trimIndent()
            }
        )
    }
}
