package club.memoni.backend.poc.dto

import club.memoni.backend.poc.dto.enums.MemoType
import java.time.LocalDate

data class CreateMemoRequest(
    val creditor: String,
    val type: MemoType,
    val debtor: String,
    val amount: Long,
    val dueDate: LocalDate? = null,
    val reason: String? = null,
    val installment: InstallmentInfo? = null,
    val memo: String? = null
)
