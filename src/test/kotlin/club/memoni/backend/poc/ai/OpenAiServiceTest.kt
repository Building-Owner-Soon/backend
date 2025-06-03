package club.memoni.backend.poc.ai

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class OpenAiServiceTest : DescribeSpec({
    val sut = OpenAiService(
        apiKey = ""
    )
    describe("analyze") {
        context("단일 상환") {
            it("테스트") {
                val result = sut.analyze("영수한테 100만원을 빌렸고 매달 3일에 20만원씩 갚을꺼야")
                print(result.block())
            }
        }
    }

})
