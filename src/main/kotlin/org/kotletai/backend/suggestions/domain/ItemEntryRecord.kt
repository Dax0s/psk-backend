package org.kotletai.backend.suggestions.domain

import java.math.BigDecimal
import java.time.Instant

data class ItemEntryRecord(
    val displayName: String,
    val productKey: String? = null,
    val quantity: BigDecimal? = null,
    val unit: String? = null,
    val enteredAt: Instant,
)
