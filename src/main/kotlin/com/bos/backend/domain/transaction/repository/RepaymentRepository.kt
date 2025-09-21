package com.bos.backend.domain.transaction.repository

import com.bos.backend.domain.transaction.entity.Repayment
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface RepaymentRepository : CoroutineCrudRepository<Repayment, Long> {
    suspend fun findByTransactionIdIn(transactionIds: List<Long>): List<Repayment>
}
