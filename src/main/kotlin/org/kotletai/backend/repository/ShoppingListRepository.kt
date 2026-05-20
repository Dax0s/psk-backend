package org.kotletai.backend.repository

import org.kotletai.backend.entity.ShoppingList
import org.kotletai.backend.entity.User
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
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

    @Query(
        """
           SELECT DISTINCT sl FROM ShoppingList sl
           LEFT JOIN FETCH sl.family f
           LEFT JOIN FETCH sl.items
           WHERE sl.user = :user
           OR f IN (SELECT fm.family FROM FamilyMember fm WHERE fm.user = :user)
       """,
    )
    fun findAllAccessibleBy(user: User): List<ShoppingList>
}
