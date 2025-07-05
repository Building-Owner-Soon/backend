package club.memoni.backend.poc.service
//
//import club.memoni.backend.poc.dto.CreateMemoRequest
//import club.memoni.backend.poc.dto.CreateMemoResponse
//import club.memoni.backend.poc.dto.InstallmentInfo
//import club.memoni.backend.poc.dto.enums.InstallmentCycle
//import club.memoni.backend.poc.dto.enums.MemoStatus
//import club.memoni.backend.poc.dto.enums.MemoType
//import org.springframework.stereotype.Service
//import java.time.LocalDate
//import java.time.LocalDateTime
//import java.time.ZonedDateTime
//import java.util.*
//
//@Service
//class MemoService {
//    fun createMemo(request: CreateMemoRequest): CreateMemoResponse {
//        return CreateMemoResponse(
//            id = UUID.randomUUID(),
//            creditor = "홍길동",
//            type = MemoType.INSTALLMENT,
//            debtor = "김철수",
//            amount = 1000000,
//            dueDate = null,
//            reason = null,
//            installment = InstallmentInfo(
//                startDate = LocalDate.of(2025, 7, 1),
//                installmentCycle = InstallmentCycle.MONTHLY,
//                installmentDay = 1,
//                installmentAmount = 100000,
//                totalInstallments = 10,
//                remainingInstallments = 1000000,
//                nextPaymentDate = LocalDate.of(2025, 7, 1),
//            ),
//            status = MemoStatus.ACTIVE,
//            remainingAmount = 1000000,
//            createdAt = ZonedDateTime.now(),
//            updatedAt = ZonedDateTime.now(),
//            creator = "나",
//            memo = null
//        )
//    }
//}