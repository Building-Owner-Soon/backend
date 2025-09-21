package com.bos.backend.presentation.transaction.controller

import com.bos.backend.application.transaction.RepaymentScheduleService
import com.bos.backend.presentation.transaction.dto.RepaymentManagementResponseDTO
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/repayment-schedules")
class RepaymentScheduleController(
    private val repaymentScheduleService: RepaymentScheduleService,
) {
    @GetMapping("/management")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getRepaymentManagement(
        @AuthenticationPrincipal userId: String,
    ): RepaymentManagementResponseDTO = repaymentScheduleService.getRepaymentManagement(userId.toLong())
}
