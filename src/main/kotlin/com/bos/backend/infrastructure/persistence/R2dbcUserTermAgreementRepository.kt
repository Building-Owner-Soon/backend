package com.bos.backend.infrastructure.persistence

import com.bos.backend.domain.term.entity.UserTermAgreement
import com.bos.backend.domain.term.repository.UserTermAgreementRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

interface UserTermsAgreementCoroutineRepository : CoroutineCrudRepository<UserTermAgreement, Long> {
    @Query("SELECT * FROM user_terms_agreement WHERE user_id = :userId AND terms_id = :termsId")
    suspend fun findByUserIdAndTermsId(
        @Param("userId") userId: Long,
        @Param("termsId") termsId: Long,
    ): UserTermAgreement?
}

@Repository
class R2dbcUserTermsAgreementRepositoryImpl(
    private val coroutineRepository: UserTermsAgreementCoroutineRepository,
) : UserTermAgreementRepository {
    override suspend fun saveAll(agreements: List<UserTermAgreement>): Flow<UserTermAgreement> =
        coroutineRepository.saveAll(agreements)

//    override suspend fun findByUserIdAndTermsId(
//        userId: Long,
//        termsId: Long,
//    ): UserTermAgreement? = coroutineRepository.findByUserIdAndTermsId(userId, termsId)
}
