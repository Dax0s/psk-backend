package org.kotletai.backend.repository

import org.kotletai.backend.entity.ShoppingList
import org.kotletai.backend.entity.ShoppingListItem
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ShoppingListItemRepository : JpaRepository<ShoppingListItem, UUID> {
    fun findByIdAndShoppingList(
        id: UUID,
        shoppingList: ShoppingList,
    ): ShoppingListItem?
}
