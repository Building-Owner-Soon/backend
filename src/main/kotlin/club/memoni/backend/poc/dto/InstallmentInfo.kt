package club.memoni.backend.poc.dto

import club.memoni.backend.poc.dto.enums.InstallmentCycle
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class InstallmentInfo(
    @JsonFormat(pattern = "yyyy-MM-dd")
    val startDate: LocalDate,         // 첫 상환일
    val installmentAmount: Int,       // 회차별 상환 금액
    val installmentCycle: InstallmentCycle,     // 상환 주기(MONTHLY, WEEKLY)
    val installmentDay: Int,          // 상환일(매달 x일)
    val totalInstallments: Int,        // 총 상환 회차
    val remainingInstallments: Int? = null,
    val nextPaymentDate: LocalDate? = null
)
