package com.bos.backend.presentation.transaction.controller

import com.bos.backend.application.transaction.TransactionService
import com.bos.backend.presentation.transaction.dto.RelationshipSummaryDTO
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/relationships")
class RelationshipController(
    private val transactionService: TransactionService,
) {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    suspend fun getRelationships(
        @AuthenticationPrincipal userId: String,
    ): List<RelationshipSummaryDTO> = transactionService.getRelationships(userId.toLong())
}
