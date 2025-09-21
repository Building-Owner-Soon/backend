package com.bos.backend.domain.transaction.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

@Table("repayment_schedules")
data class RepaymentSchedule(
    @Id
    val id: Long? = null,
    @Column("transaction_id")
    val transactionId: Long,
    @Column("repayment_date")
    val repaymentDate: LocalDate,
    @Column("repayment_amount")
    val repaymentAmount: BigDecimal,
    @Column("created_at")
    val createdAt: Instant = Instant.now(),
    @Column("updated_at")
    val updatedAt: Instant = Instant.now(),
)
