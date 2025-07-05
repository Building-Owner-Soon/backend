package club.memoni.backend.poc.dto

import club.memoni.backend.poc.dto.enums.HistoryType
import java.time.ZonedDateTime

data class HistoryItem(
    val date: ZonedDateTime,
    val amount: Long,
    val type: HistoryType,
    val note: String? = null
)
