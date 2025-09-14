package com.bos.backend.domain.transaction.enum

enum class RelationshipType(val description: String) {
    PARENT("부모님"),
    SIBLING("형제자매"),
    FRIEND("친구"),
    LOVER("애인"),
    OTHER("기타"),
}
