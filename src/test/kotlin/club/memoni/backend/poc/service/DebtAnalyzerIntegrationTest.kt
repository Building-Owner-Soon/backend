package club.memoni.backend.poc.service

import club.memoni.backend.poc.ai.MockLLMService
import club.memoni.backend.poc.config.JacksonConfiguration
import club.memoni.backend.poc.model.PaymentType
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.LocalDate

class DebtAnalyzerIntegrationTest : DescribeSpec() {

    override fun extensions() = listOf(SpringExtension)

    init {
        this.isolationMode = io.kotest.core.spec.IsolationMode.InstancePerLeaf

        lateinit var debtAnalyzerService: DebtAnalyzerService

        beforeSpec {
            debtAnalyzerService = DebtAnalyzerService(
                llmService = MockLLMService(),
                objectMapper = JacksonConfiguration().objectMapper()
            )
        }

        describe("채무 분석 서비스 통합 테스트") {
            context("일시불 상환 정보 처리") {
                val text = "지영이한테 5만원 빌렸고, 6월 15일까지 갚기로 했어"

                it("전체 프로세스가 정상적으로 작동해야 함") {
                    val result = debtAnalyzerService.analyze(text).block()

                    result shouldNotBe null
                    result?.creditor shouldBe "지영"
                    result?.type shouldBe PaymentType.SINGLE
                    result?.amount shouldBe 50000
                    result?.dueDate shouldBe LocalDate.of(2025, 6, 15)
                }
            }

            context("분할 상환 정보 처리") {
                val text = "홍길동한테 50만원을 빌렸고, 다음달 부터 매달 5일에 10만원씩 상환할거야"

                it("분할 상환 정보가 올바르게 처리되어야 함") {
                    val result = debtAnalyzerService.analyze(text).block()

                    result shouldNotBe null
                    result?.creditor shouldBe "홍길동"
                    result?.type shouldBe PaymentType.INSTALLMENT
                    result?.amount shouldBe 500000

                    val installment = result?.installment
                    installment shouldNotBe null
                    installment?.startDate shouldBe LocalDate.of(2025, 7, 5)
                    installment?.installmentAmount shouldBe 100000
                    installment?.installmentCycle shouldBe "MONTHLY"
                    installment?.totalInstallments shouldBe 5
                }
            }

            context("다양한 표현 처리 능력") {
                val texts = listOf(
                    "철수에게 30만원 빌렸고, 이유는 급한 병원비 때문이야. 다음주 월요일까지 갚을게",
                    "지영한테 5만원 빌렸는데, 내 생일 선물 사려고 빌렸어. 다음달 10일에 갚기로 했어",
                    "회사에서 200만원 빌렸고, 3개월동안 매달 15일에 70만원씩 갚기로 했어"
                )

                for ((index, text) in texts.withIndex()) {
                    it("다양한 표현 케이스 #${index + 1}가 올바르게 처리되어야 함") {
                        val result = debtAnalyzerService.analyze(text).block()

                        result shouldNotBe null
                        when (index) {
                            0 -> {
                                result?.creditor shouldBe "철수"
                                result?.reason shouldBe "급한 병원비"
                            }
                            1 -> {
                                result?.creditor shouldBe "지영"
                                result?.reason shouldBe "생일 선물"
                            }
                            2 -> {
                                result?.creditor shouldBe "회사"
                                result?.type shouldBe PaymentType.INSTALLMENT
                                result?.installment?.totalInstallments shouldBe 3
                            }
                        }
                    }
                }
            }
        }
    }
}
