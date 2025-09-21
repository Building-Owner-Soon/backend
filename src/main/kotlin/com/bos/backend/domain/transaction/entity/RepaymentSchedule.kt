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
    @Column("scheduled_date")
    val scheduledDate: LocalDate,
    @Column("scheduled_amount")
    val scheduledAmount: BigDecimal,
    @Column("actual_date")
    val actualDate: LocalDate?,
    @Column("actual_amount")
    val actualAmount: BigDecimal?,
    @Column("created_at")
    val createdAt: Instant = Instant.now(),
    @Column("updated_at")
    val updatedAt: Instant = Instant.now(),
) {
    fun complete(
        actualDate: LocalDate,
        actualAmount: BigDecimal,
    ): RepaymentSchedule =
        this.copy(
            actualDate = actualDate,
            actualAmount = actualAmount,
            updatedAt = Instant.now(),
        )
}
