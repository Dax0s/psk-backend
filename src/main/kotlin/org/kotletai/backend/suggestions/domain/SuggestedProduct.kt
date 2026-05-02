package org.kotletai.backend.suggestions.domain

import java.math.BigDecimal
import java.time.Instant

data class SuggestedProduct(
    val productKey: String,
    val displayName: String,
    val suggestedQuantity: BigDecimal?,
    val unit: String?,
    val purchaseCount: Int,
    val lastCompletedAt: Instant,
    val suggestionWeight: Int,
)
