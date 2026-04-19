package org.kotletai.backend.repository

import org.kotletai.backend.entity.ShoppingListItem
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ShoppingListItemRepository : JpaRepository<ShoppingListItem, UUID>
