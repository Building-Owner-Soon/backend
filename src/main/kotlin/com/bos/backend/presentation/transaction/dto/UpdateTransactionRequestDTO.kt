package com.bos.backend.presentation.transaction.dto

import com.bos.backend.domain.transaction.enum.RelationshipType
import com.bos.backend.domain.user.entity.Character
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class UpdateTransactionRequestDTO(
    @field:NotBlank(message = "상대방 이름은 필수입니다")
    val counterpartName: String,
    @field:NotNull(message = "상대방 캐릭터는 필수입니다")
    val counterpartCharacter: Character,
    @field:NotNull(message = "관계는 필수입니다")
    val relationship: RelationshipType,
    val customRelationship: String?,
    val memo: String?,
)
