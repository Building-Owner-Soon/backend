package com.bos.backend.domain.term.repository

import com.bos.backend.domain.term.entity.UserTermAgreement

interface UserTermAgreementRepository {
    suspend fun saveAll(agreements: List<UserTermAgreement>): List<UserTermAgreement>

    suspend fun findByUserIdAndTermsId(
        userId: Long,
        termsId: Long,
    ): UserTermAgreement?
}
