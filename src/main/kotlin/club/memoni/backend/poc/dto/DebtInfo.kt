package club.memoni.backend.poc.dto

import club.memoni.backend.poc.dto.enums.PaymentType
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class DebtInfo(
    val creditor: String?,            // 채권자(돈을 빌려준 사람)
    val type: PaymentType,            // 상환 유형
    val debtor: String = "나",         // 채무자(돈을 빌린 사람)
    val amount: Int?,                 // 금액(원)
    @JsonFormat(pattern = "yyyy-MM-dd")
    val dueDate: LocalDate? = null,   // 상환 예정일(일시불용)
    val reason: String? = null,       // 빌린 이유
    val installment: InstallmentInfo? = null  // 분할 상환 정보
)
