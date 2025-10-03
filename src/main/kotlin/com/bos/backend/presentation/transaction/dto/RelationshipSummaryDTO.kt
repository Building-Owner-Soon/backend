package com.bos.backend.presentation.transaction.dto

import com.bos.backend.domain.transaction.enum.RelationshipType
import com.bos.backend.domain.user.entity.Character

data class RelationshipSummaryDTO(
    val counterpartName: String,
    val counterpartCharacter: Character,
    val relationship: RelationshipType,
    val customRelationship: String?,
    val lendAmount: Long,
    val borrowAmount: Long,
    val upcomingTransactionInfo: UpcomingTransactionInfoDTO?,
)
