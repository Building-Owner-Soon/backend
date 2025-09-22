package com.bos.backend.domain.transaction.enum

enum class TransactionType(val description: String) {
    LEND("갚을돈"),
    BORROW("받을돈"),
}
