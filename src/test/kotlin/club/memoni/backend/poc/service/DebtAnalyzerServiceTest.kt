package club.memoni.backend.poc.service

import club.memoni.backend.poc.ai.LLMService
import club.memoni.backend.poc.config.JacksonConfiguration
import club.memoni.backend.poc.model.PaymentType
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import reactor.core.publisher.Mono
import java.time.LocalDate

class DebtAnalyzerServiceTest : DescribeSpec({
    val llmService = mockk<LLMService>()
    val objectMapper = JacksonConfiguration().objectMapper()
    val debtAnalyzerService = DebtAnalyzerService(llmService, objectMapper)

    describe("DebtAnalyzerService") {
        context("단일 상환 정보 분석") {
            val text = "지영이한테 5만원 빌렸고, 6월 15일까지 갚기로 했어"
            val mockResponse = """
                {
                  "creditor": "지영",
                  "type": "SINGLE",
                  "debtor": "나",
                  "amount": 50000,
                  "dueDate": "2025-06-15",
                  "reason": null
                }
            """.trimIndent()

            every { llmService.analyze(text) } returns Mono.just(mockResponse)

            it("채권자, 금액, 만기일이 올바르게 추출되어야 함") {
                val result = debtAnalyzerService.analyze(text).block()

                result shouldNotBe null
                result?.creditor shouldBe "지영"
                result?.type shouldBe PaymentType.SINGLE
                result?.amount shouldBe 50000
                result?.dueDate shouldBe LocalDate.of(2025, 6, 15)
                result?.reason shouldBe null
                result?.installment shouldBe null
            }
        }

        context("분할 상환 정보 분석") {
            val text = "홍길동한테 50만원을 빌렸고, 다음달 부터 매달 5일에 10만원씩 상환할거야"
            val mockResponse = """
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

            every { llmService.analyze(text) } returns Mono.just(mockResponse)

            it("분할 상환 정보가 올바르게 추출되어야 함") {
                val result = debtAnalyzerService.analyze(text).block()

                result shouldNotBe null
                result?.creditor shouldBe "홍길동"
                result?.type shouldBe PaymentType.INSTALLMENT
                result?.amount shouldBe 500000
                result?.dueDate shouldBe null

                val installment = result?.installment
                installment shouldNotBe null
                installment?.startDate shouldBe LocalDate.of(2025, 7, 5)
                installment?.installmentAmount shouldBe 100000
                installment?.installmentCycle shouldBe "MONTHLY"
                installment?.installmentDay shouldBe 5
                installment?.totalInstallments shouldBe 5
            }
        }

        context("이유가 포함된 채무 정보 분석") {
            val text = "철수에게 30만원 빌렸고, 이유는 급한 병원비 때문이야. 다음주 월요일까지 갚을게"
            val mockResponse = """
                {
                  "creditor": "철수",
                  "type": "SINGLE",
                  "debtor": "나",
                  "amount": 300000,
                  "dueDate": "2025-06-08",
                  "reason": "급한 병원비"
                }
            """.trimIndent()

            every { llmService.analyze(text) } returns Mono.just(mockResponse)

            it("빌린 이유가 올바르게 추출되어야 함") {
                val result = debtAnalyzerService.analyze(text).block()

                result shouldNotBe null
                result?.creditor shouldBe "철수"
                result?.amount shouldBe 300000
                result?.reason shouldBe "급한 병원비"
                result?.dueDate shouldBe LocalDate.of(2025, 6, 8)
            }
        }

        context("복잡한 분할 상환 정보 분석") {
            val text = "회사에서 200만원 빌렸고, 3개월동안 매달 15일에 70만원씩 갚기로 했어"
            val mockResponse = """
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

            every { llmService.analyze(text) } returns Mono.just(mockResponse)

            it("복잡한 분할 상환 정보가 올바르게 추출되어야 함") {
                val result = debtAnalyzerService.analyze(text).block()

                result shouldNotBe null
                result?.creditor shouldBe "회사"
                result?.type shouldBe PaymentType.INSTALLMENT
                result?.amount shouldBe 2000000

                val installment = result?.installment
                installment shouldNotBe null
                installment?.installmentAmount shouldBe 700000
                installment?.totalInstallments shouldBe 3
                installment?.installmentCycle shouldBe "MONTHLY"
                installment?.installmentDay shouldBe 15
            }
        }

        context("LLM 응답 파싱 실패") {
            val text = "이상한 입력"
            val invalidResponse = "{ invalid json }"

            every { llmService.analyze(text) } returns Mono.just(invalidResponse)

            it("예외가 발생해야 함") {
                val resultMono = debtAnalyzerService.analyze(text)

                resultMono.doOnError { error ->
                    error shouldBe java.lang.IllegalStateException::class
                }
            }
        }
    }
})
