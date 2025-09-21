package com.bos.backend.domain.transaction.repository

import com.bos.backend.domain.transaction.entity.RepaymentSchedule
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface RepaymentScheduleRepository : CoroutineCrudRepository<RepaymentSchedule, Long> {
    suspend fun findByTransactionId(transactionId: Long): List<RepaymentSchedule>
}
