package com.bos.backend.presentation.transaction.controller

import com.bos.backend.application.transaction.RepaymentScheduleService
import com.bos.backend.application.transaction.TransactionService
import com.bos.backend.presentation.transaction.dto.CreateRepaymentRequestDTO
import com.bos.backend.presentation.transaction.dto.CreateTransactionRequestDTO
import com.bos.backend.presentation.transaction.dto.DebtSummaryResponseDTO
import com.bos.backend.presentation.transaction.dto.RepaymentManagementResponseDTO
import com.bos.backend.presentation.transaction.dto.RepaymentScheduleItemDTO
import com.bos.backend.presentation.transaction.dto.TransactionResponseDTO
import com.bos.backend.presentation.transaction.dto.UpdateTransactionRequestDTO
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/transactions")
class TransactionController(
    private val transactionService: TransactionService,
    private val repaymentScheduleService: RepaymentScheduleService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createTransaction(
        @AuthenticationPrincipal userId: String,
        @Valid @RequestBody createTransactionRequestDTO: CreateTransactionRequestDTO,
    ): TransactionResponseDTO = transactionService.createTransaction(userId.toLong(), createTransactionRequestDTO)

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getTransactionDetail(
        @AuthenticationPrincipal userId: String,
        @PathVariable id: Long,
    ): TransactionResponseDTO = transactionService.getTransactionDetail(userId.toLong(), id)

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    suspend fun updateTransaction(
        @AuthenticationPrincipal userId: String,
        @PathVariable id: Long,
        @Valid @RequestBody updateTransactionRequestDTO: UpdateTransactionRequestDTO,
    ): TransactionResponseDTO = transactionService.updateTransaction(userId.toLong(), id, updateTransactionRequestDTO)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteTransaction(
        @AuthenticationPrincipal userId: String,
        @PathVariable id: Long,
    ): Unit = transactionService.deleteTransaction(userId.toLong(), id)

    @GetMapping("/{id}/repayment-schedules")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getRepaymentManagement(
        @AuthenticationPrincipal userId: String,
        @PathVariable id: Long,
    ): RepaymentManagementResponseDTO = repaymentScheduleService.getRepaymentManagement(userId.toLong(), id)

    @PostMapping("/{transactionId}/schedules/{scheduleId}/repayments")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun addRepayment(
        @AuthenticationPrincipal userId: String,
        @PathVariable transactionId: Long,
        @PathVariable scheduleId: Long,
        @Valid @RequestBody createRepaymentRequestDTO: CreateRepaymentRequestDTO,
    ): RepaymentScheduleItemDTO =
        repaymentScheduleService.addRepayment(
            userId.toLong(),
            transactionId,
            scheduleId,
            createRepaymentRequestDTO,
        )

    @GetMapping("/summary")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getTransactionSummary(
        @AuthenticationPrincipal userId: String,
    ): DebtSummaryResponseDTO = transactionService.getTransactionSummary(userId.toLong())
}
