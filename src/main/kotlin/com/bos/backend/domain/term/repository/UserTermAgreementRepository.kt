package com.bos.backend.domain.term.repository

import com.bos.backend.domain.term.entity.UserTermAgreement
import kotlinx.coroutines.flow.Flow

interface UserTermAgreementRepository {
    suspend fun saveAll(agreements: List<UserTermAgreement>): Flow<UserTermAgreement>
}
