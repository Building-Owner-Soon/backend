package com.bos.backend.domain.term.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table(name = "terms")
data class Term(
    @Id
    val id: Long? = null,
    @Column("code")
    val code: String,
    @Column("title")
    val title: String,
    @Column("content")
    val content: String,
    @Column("is_required")
    val isRequired: Boolean = false,
    @Column("version")
    val version: String,
    @Column("created_at")
    val createdAt: Instant = Instant.now(),
)
