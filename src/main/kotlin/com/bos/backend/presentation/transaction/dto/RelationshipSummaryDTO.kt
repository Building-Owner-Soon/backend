package com.bos.backend.presentation.transaction.dto

import com.bos.backend.domain.transaction.entity.CounterpartCharacter
import com.bos.backend.domain.transaction.enum.RelationshipType

data class RelationshipSummaryDTO(
    val counterpartName: String,
    val counterpartCharacter: CounterpartCharacter,
    val relationship: RelationshipType,
    val customRelationship: String?,
    val lendAmount: Long,
    val borrowAmount: Long,
    val upcomingTransactionInfo: UpcomingTransactionInfoDTO?,
    val transactionId: Long,
)
