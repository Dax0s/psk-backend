package org.kotletai.backend.repository

import org.kotletai.backend.entity.ShoppingList
import org.kotletai.backend.entity.User
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ShoppingListRepository : JpaRepository<ShoppingList, UUID> {
    @EntityGraph(attributePaths = ["items"])
    fun findByUser(user: User): List<ShoppingList>

    @EntityGraph(attributePaths = ["items"])
    fun findByIdAndUser(
        id: UUID,
        user: User,
    ): ShoppingList?

    fun findByFamilyId(familyId: UUID): List<ShoppingList>
}
