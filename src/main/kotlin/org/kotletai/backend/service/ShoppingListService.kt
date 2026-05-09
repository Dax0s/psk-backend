package org.kotletai.backend.service

import jakarta.transaction.Transactional
import org.kotletai.backend.config.CurrentUser
import org.kotletai.backend.entity.ProductCategory
import org.kotletai.backend.entity.ShoppingList
import org.kotletai.backend.entity.ShoppingListItem
import org.kotletai.backend.exception.NotFoundException
import org.kotletai.backend.repository.ShoppingListItemRepository
import org.kotletai.backend.repository.ShoppingListRepository
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

    fun updateShoppingList(
        id: UUID,
        name: String,
    ): ShoppingList {
        val shoppingList =
            shoppingListRepository.findByIdAndUser(id, currentUser.user)
                ?: throw NotFoundException("Shopping list with ID: $id not found")

        return shoppingListRepository.save(
            ShoppingList(
                name,
                shoppingList.user,
                shoppingList.items,
                shoppingList.id,
            ),
        )
    }

    fun deleteShoppingList(id: UUID) {
        val shoppingList =
            shoppingListRepository.findByIdAndUser(id, currentUser.user)
                ?: throw NotFoundException("Shopping list with ID: $id not found")

        shoppingListRepository.delete(shoppingList)
    }

    fun createShoppingListItem(
        id: UUID,
        name: String,
        quantity: BigDecimal,
        category: ProductCategory? = null,
    ): ShoppingListItem {
        val shoppingList =
            shoppingListRepository.findByIdAndUser(id, currentUser.user)
                ?: throw NotFoundException("Shopping list with ID: $id not found")

        return shoppingListItemRepository.save(
            ShoppingListItem(shoppingList, name, quantity, category = category ?: ProductCategory.OTHER),
        )
    }

    fun updateShoppingListItem(
        id: UUID,
        itemId: UUID,
        name: String,
        quantity: BigDecimal,
        checked: Boolean,
        category: ProductCategory? = null,
    ): ShoppingListItem {
        val shoppingList =
            shoppingListRepository.findByIdAndUser(id, currentUser.user)
                ?: throw NotFoundException("Shopping list with ID: $id not found")

        val shoppingListItem =
            shoppingListItemRepository.findByIdAndShoppingList(itemId, shoppingList)
                ?: throw NotFoundException("Shopping list item with ID: $itemId not found")

        return shoppingListItemRepository.save(
            ShoppingListItem(
                shoppingList,
                name,
                quantity,
                checked,
                category ?: shoppingListItem.category,
                shoppingListItem.id,
            ),
        )
    }

    @Transactional
    fun deleteShoppingListItem(
        id: UUID,
        itemId: UUID,
    ) {
        val shoppingList =
            shoppingListRepository.findByIdAndUser(id, currentUser.user)
                ?: throw NotFoundException("Shopping list with ID: $id not found")

        val removed = shoppingList.items.removeIf { it.id == itemId }

        if (!removed) {
            throw NotFoundException("Shopping list item with ID: $id not found")
        }

        shoppingListRepository.save(shoppingList)
    }
}
