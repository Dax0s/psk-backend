package org.kotletai.backend.suggestions.domain

import java.math.BigDecimal
import java.time.Instant

data class CompletedPurchaseRecord(
    val displayName: String,
    val productKey: String? = null,
    val quantity: BigDecimal? = null,
    val unit: String? = null,
    val completedAt: Instant,
    val signalWeight: Int = 1,
)
