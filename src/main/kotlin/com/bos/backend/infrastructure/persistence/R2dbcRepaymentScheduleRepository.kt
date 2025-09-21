package com.bos.backend.infrastructure.persistence

import com.bos.backend.domain.transaction.entity.RepaymentSchedule
import com.bos.backend.domain.transaction.repository.RepaymentScheduleRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface R2dbcRepaymentScheduleRepository :
    RepaymentScheduleRepository,
    CoroutineCrudRepository<RepaymentSchedule, Long>
