package org.kotletai.backend.model

import org.kotletai.backend.entity.ShoppingListItem
import java.math.BigDecimal
import java.util.UUID

data class ShoppingListItemResponse(
    val id: UUID,
    val name: String,
    val quantity: BigDecimal,
    val checked: Boolean,
)

fun ShoppingListItem.toResponse() =
    ShoppingListItemResponse(
        id = this.id!!,
        name = this.name,
        quantity = this.quantity,
        checked = this.checked,
    )
