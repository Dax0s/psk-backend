package org.kotletai.backend.suggestions.domain

import java.math.BigDecimal
import java.time.Instant

data class PinnedProduct(
    val id: Long,
    val scope: SuggestionScope,
    val productKey: String,
    val displayName: String,
    val defaultQuantity: BigDecimal?,
    val unit: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
)
