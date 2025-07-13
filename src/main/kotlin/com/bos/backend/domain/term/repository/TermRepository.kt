package com.bos.backend.domain.term.repository

import com.bos.backend.domain.term.entity.Term
import kotlinx.coroutines.flow.Flow

interface TermRepository {
    suspend fun findById(id: Long): Term?

    suspend fun findAllByIds(ids: List<Long>): Flow<Term>
}
