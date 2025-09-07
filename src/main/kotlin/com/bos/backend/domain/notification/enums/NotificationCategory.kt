package com.bos.backend.domain.notification.enums

enum class NotificationCategory(
    val displayName: String,
    val iconType: String,
) {
    REPAYMENT_DUE("돈 갚는날", "repayment_due"),
    REPAYMENT_COMPLETED("돈 갚기 완료", "repayment_completed"),
    RECEIVABLE_DUE("돈 받는날", "receivable_due"),
    RECEIVABLE_COMPLETED("돈 받기 완료", "receivable_completed"),
    GENERAL("일반", "general"),
}
