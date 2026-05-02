package org.kotletai.backend.suggestions.persistence

import java.math.BigDecimal

data class ShoppingListItemSuggestionRecord(
    val name: String,
    val quantity: BigDecimal,
    val checked: Boolean,
)
