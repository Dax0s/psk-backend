package org.kotletai.backend.repository

import org.kotletai.backend.entity.ShoppingList
import org.kotletai.backend.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface ShoppingListRepository : JpaRepository<ShoppingList, UUID> {
    fun findByUser(user: User): List<ShoppingList>

    fun findByIdAndUser(
        id: UUID,
        user: User,
    ): ShoppingList?
}
