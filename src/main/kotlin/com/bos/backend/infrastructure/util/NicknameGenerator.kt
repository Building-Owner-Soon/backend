package com.bos.backend.infrastructure.util

import kotlin.random.Random

object NicknameGenerator {
    private const val MIN_NUMBER = 1000
    private const val MAX_NUMBER = 9999

    private val adjectives =
        listOf(
            "행복한", "즐거운", "멋진", "귀여운", "똑똑한",
            "용감한", "친절한", "활발한", "성실한", "밝은",
            "따뜻한", "시원한", "건강한", "재미있는", "신나는",
        )

    private val nouns =
        listOf(
            "건물주", "투자자", "개발자", "매니저", "리더",
            "파트너", "전문가", "창업가", "기획자", "디자이너",
            "분석가", "컨설턴트", "어드바이저", "플래너", "코디네이터",
        )

    fun generateRandomNickname(): String {
        val adjective = adjectives[Random.nextInt(adjectives.size)]
        val noun = nouns[Random.nextInt(nouns.size)]
        val number = Random.nextInt(MIN_NUMBER, MAX_NUMBER)
        return "$adjective$noun$number"
    }
}
