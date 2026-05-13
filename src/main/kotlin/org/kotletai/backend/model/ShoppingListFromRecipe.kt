package org.kotletai.backend.model

import kotlinx.serialization.Serializable
import org.kotletai.backend.config.BigDecimalSerializer
import java.math.BigDecimal

@Serializable
data class ShoppingListFromRecipe(
    val items: List<GeminiShoppingListItem>,
)

@Serializable
data class GeminiShoppingListItem(
    val name: String,
    @Serializable(with = BigDecimalSerializer::class)
    val quantity: BigDecimal,
)
