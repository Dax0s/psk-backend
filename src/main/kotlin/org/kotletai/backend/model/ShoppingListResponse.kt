package org.kotletai.backend.model

import org.kotletai.backend.entity.ShoppingList
import java.util.UUID

data class ShoppingListResponse(
    val id: UUID,
    val name: String,
    val items: List<ShoppingListItemResponse>,
    val familyId: UUID? = null,
    val familyName: String? = null,
)

fun ShoppingList.toResponse() =
    ShoppingListResponse(
        id = this.id!!,
        name = this.name,
        items = this.items.map { it.toResponse() },
        familyId = this.family?.id,
        familyName = this.family?.name,
    )
