package com.bos.backend.infrastructure.template

object EmailTemplate {
    object Verification {
        val SUBJECT = "[Building Owner Soon] 이메일 인증 코드"
        val CONTENT = """
            안녕하세요!
            
            내꿈은 건물주 이메일 인증 코드입니다.
            
            인증 코드: {code}
            
            이 코드는 10분 후에 만료됩니다.
            본인이 요청하지 않은 경우 이 메일을 무시하세요.
            
            감사합니다.
        """.trimIndent()
    }
    
} 