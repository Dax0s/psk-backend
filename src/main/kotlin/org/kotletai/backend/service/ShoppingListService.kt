package org.kotletai.backend.service

import org.kotletai.backend.config.CurrentUser
import org.kotletai.backend.entity.ShoppingList
import org.kotletai.backend.entity.ShoppingListItem
import org.kotletai.backend.exception.NotFoundException
import org.kotletai.backend.repository.ShoppingListItemRepository
import org.kotletai.backend.repository.ShoppingListRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.UUID

@Service
class ShoppingListService(
    private val currentUser: CurrentUser,
    private val shoppingListRepository: ShoppingListRepository,
    private val shoppingListItemRepository: ShoppingListItemRepository,
) {
    fun getShoppingLists(): List<ShoppingList> = shoppingListRepository.findByUser(currentUser.user)

    fun getShoppingList(id: UUID): ShoppingList =
        shoppingListRepository.findByIdAndUser(id, currentUser.user)
            ?: throw NotFoundException("Shopping list with ID: $id not found")

    fun createShoppingList(name: String): ShoppingList = shoppingListRepository.save(ShoppingList(name, currentUser.user, mutableListOf()))

    fun deleteShoppingList(id: UUID) {
        val shoppingList =
            shoppingListRepository.findByIdOrNull(id) ?: throw NotFoundException("Shopping list with ID: $id not found")

        shoppingListRepository.delete(shoppingList)
    }

    fun createShoppingListItem(
        id: UUID,
        name: String,
        quantity: BigDecimal,
    ): ShoppingListItem {
        val shoppingList =
            shoppingListRepository.findByIdOrNull(id) ?: throw NotFoundException("Shopping list with ID: $id not found")

        return shoppingListItemRepository.save(ShoppingListItem(shoppingList, name, quantity))
    }
}
