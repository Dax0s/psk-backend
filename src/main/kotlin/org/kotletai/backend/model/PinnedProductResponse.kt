package org.kotletai.backend.model

import org.kotletai.backend.entity.PinnedProduct
import org.kotletai.backend.entity.ProductCategory
import java.math.BigDecimal
import java.util.UUID

data class PinnedProductResponse(
    val id: UUID,
    val name: String,
    val defaultQuantity: BigDecimal?,
    val sortOrder: Int,
    val category: ProductCategory,
)

fun PinnedProduct.toResponse() =
    PinnedProductResponse(
        id = this.id!!,
        name = this.name,
        defaultQuantity = this.defaultQuantity,
        sortOrder = this.sortOrder,
        category = this.category,
    )
