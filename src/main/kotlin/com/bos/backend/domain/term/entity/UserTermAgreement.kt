package com.bos.backend.domain.term.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("user_terms_agreement")
data class UserTermAgreement(
    @Id
    val id: Long? = null,
    val userId: Long,
    val termsId: Long,
    val agreedAt: Instant? = null,
    val revokedAt: Instant? = null,
) {
    fun isActive(): Boolean = revokedAt == null
}
