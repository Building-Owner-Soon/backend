package com.bos.backend.infrastructure.persistence

import com.bos.backend.domain.transaction.entity.Repayment
import com.bos.backend.domain.transaction.repository.RepaymentRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface R2dbcRepaymentRepository :
    RepaymentRepository,
    CoroutineCrudRepository<Repayment, Long>
