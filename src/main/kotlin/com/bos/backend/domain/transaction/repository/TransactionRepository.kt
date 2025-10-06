package com.bos.backend.domain.transaction.repository

import com.bos.backend.domain.transaction.entity.Transaction

interface TransactionRepository {
    suspend fun save(transaction: Transaction): Transaction

    suspend fun findById(id: Long): Transaction?

    suspend fun findByUserId(userId: Long): List<Transaction>

    suspend fun deleteById(id: Long)
}
