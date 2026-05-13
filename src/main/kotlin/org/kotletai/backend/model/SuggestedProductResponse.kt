package org.kotletai.backend.model

import org.kotletai.backend.entity.ProductCategory
import java.math.BigDecimal

data class SuggestedProductResponse(
    val name: String,
    val suggestedQuantity: BigDecimal,
    val entryCount: Int,
    val category: ProductCategory,
)
