package club.memoni.backend.poc.ai

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class OpenAiService(
    @Value("\${openai.api.key}")
    private val apiKey: String
) : LLMService {
    private val openAI by lazy {
        OpenAI(apiKey)
    }

    override fun analyze(text: String): Mono<String> = mono {
        val currentDate = LocalDate.now()
        val systemPrompt = """
            당신은 사용자의 텍스트에서 채무 및 정산 정보를 추출하는 전문가입니다.
            사용자가 제공하는 텍스트에서 다음 정보를 추출하여 JSON 형식으로 반환해주세요:
            
            1. creditor: 채권자(돈을 빌려준 사람)
            2. type: 상환 유형 ("SINGLE" 또는 "INSTALLMENT")
            3. debtor: 채무자(돈을 빌린 사람), 기본값은 "나"
            4. amount: 금액(원)
            5. dueDate: 상환 예정일(일시불인 경우), "yyyy-MM-dd" 형식
            6. reason: 빌린 이유(있는 경우)
            7. installment: 분할 상환 정보(분할 상환인 경우)
               a. startDate: 첫 상환일, "yyyy-MM-dd" 형식
               b. installmentAmount: 회차별 상환 금액
               c. installmentCycle: 상환 주기("MONTHLY" 또는 "WEEKLY")
               d. installmentDay: 상환일(매달 x일)
               e. totalInstallments: 총 상환 회차
            
            오늘 날짜는 ${currentDate.format(DateTimeFormatter.ISO_DATE)}입니다.
            응답은 JSON 형식으로만 제공해 주세요. 추가 설명이나 텍스트 없이 JSON 객체만 반환하세요.
            
            예시 입력: "지영이한테 5만원 빌렸고, 6월 15일까지 갚기로 했어"
            예시 출력: 
            {
              "creditor": "지영",
              "type": "SINGLE",
              "debtor": "나",
              "amount": 50000,
              "dueDate": "2025-06-15",
              "reason": null
            }
            
            예시 입력: "홍길동한테 50만원을 빌렸고, 다음달 부터 매달 5일에 10만원씩 상환할거야"
            예시 출력:
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

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = systemPrompt
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = text
                )
            )
        )

        val completion = openAI.chatCompletion(chatCompletionRequest)
        completion.choices.first().message.content ?: "{}"
    }
}
