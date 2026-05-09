package org.kotletai.backend.model

import java.math.BigDecimal

data class SuggestedProductResponse(
    val name: String,
    val suggestedQuantity: BigDecimal,
    val entryCount: Int,
)
