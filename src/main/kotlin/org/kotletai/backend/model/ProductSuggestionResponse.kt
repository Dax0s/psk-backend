package org.kotletai.backend.model

import org.kotletai.backend.entity.ProductCategory
import org.kotletai.backend.entity.ProductOffer
import java.math.BigDecimal
import java.util.UUID

data class ProductSuggestionResponse(
    val id: UUID,
    val name: String,
    val category: ProductCategory,
    val price: BigDecimal?,
    val oldPrice: String?,
    val discountPct: Int?,
    val unitPrice: String?,
    val validTo: String?,
    val promo: String?,
    val imageUrl: String?,
)

fun ProductOffer.toResponse() =
    ProductSuggestionResponse(
        id = this.id!!,
        name = this.name,
        category = ProductCategory.fromMaxima(this.category),
        price = this.price,
        oldPrice = this.oldPrice,
        discountPct = this.discountPct,
        unitPrice = this.unitPrice,
        validTo = this.validTo,
        promo = this.promo,
        imageUrl = this.imageUrl,
    )
