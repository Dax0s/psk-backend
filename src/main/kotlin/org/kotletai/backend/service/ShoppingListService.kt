package org.kotletai.backend.service

import org.kotletai.backend.config.CurrentUser
import org.kotletai.backend.entity.ProductCategory
import org.kotletai.backend.entity.ShoppingList
import org.kotletai.backend.entity.ShoppingListItem
import org.kotletai.backend.exception.ForbiddenException
import org.kotletai.backend.exception.NotFoundException
import org.kotletai.backend.repository.FamilyMemberRepository
import org.kotletai.backend.repository.FamilyRepository
import org.kotletai.backend.repository.ShoppingListItemRepository
import org.kotletai.backend.repository.ShoppingListRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@Service
class ShoppingListService(
    private val currentUser: CurrentUser,
    private val shoppingListRepository: ShoppingListRepository,
    private val shoppingListItemRepository: ShoppingListItemRepository,
    private val familyMemberRepository: FamilyMemberRepository,
    private val familyRepository: FamilyRepository,
    private val shoppingListFromRecipeGenerator: ShoppingListFromRecipeGenerator,
) {
    @Transactional
    fun getShoppingLists(): List<ShoppingList> =
        shoppingListRepository.findByUser(currentUser.user).onEach { list ->
            list.items.size
            list.family?.name
        }

    private fun requireFamilyAccess(id: UUID): ShoppingList {
        val user = currentUser.user
        val list = shoppingListRepository.findById(id).orElseThrow {
            NotFoundException("Shopping list with ID: $id not found")
        }
        val isOwner = list.user.id == user.id
        val isFamilyMember =
            list.family != null &&
                familyMemberRepository.findByFamilyIdAndUserCognitoId(list.family!!.id!!, user.cognitoId) != null
        if (!isOwner && !isFamilyMember) {
            throw ForbiddenException("You do not have access to this shopping list.")
        }
        return list
    }

    @Transactional
    fun getShoppingList(id: UUID): ShoppingList {
        val list = requireFamilyAccess(id)
        list.items.size
        list.family?.name
        return list
    }

    fun createShoppingList(
        name: String,
        familyId: UUID?,
    ): ShoppingList {
        val user = currentUser.user
        val family =
            if (familyId != null) {
                familyRepository
                    .findById(familyId)
                    .orElseThrow { NotFoundException("Family not found.") }
                    .also {
                        familyMemberRepository.findByFamilyIdAndUserCognitoId(familyId, user.cognitoId)
                            ?: throw ForbiddenException("You are not a member of this family.")
                    }
            } else {
                null
            }
        return shoppingListRepository.save(ShoppingList(name, user, mutableListOf(), family))
    }

    @Transactional
    fun updateShoppingList(
        id: UUID,
        name: String
    ): ShoppingList {
        val shoppingList = requireFamilyAccess(id)
        shoppingList.name = name
        val saved = shoppingListRepository.save(shoppingList)
        saved.items.size
        saved.family?.name
        return saved
    }

    @Transactional
    fun deleteShoppingList(id: UUID) {
        val shoppingList = requireFamilyAccess(id)
        shoppingListRepository.delete(shoppingList)
    }

    @Transactional
    fun createShoppingListItem(
        id: UUID,
        name: String,
        quantity: BigDecimal,
        category: ProductCategory? = null,
    ): ShoppingListItem {
        val shoppingList = requireFamilyAccess(id)
        return shoppingListItemRepository.save(
            ShoppingListItem(shoppingList, name, quantity, category = category ?: ProductCategory.OTHER),
        )
    }

    @Transactional
    fun updateShoppingListItem(
        id: UUID,
        itemId: UUID,
        name: String,
        quantity: BigDecimal,
        checked: Boolean,
        category: ProductCategory? = null,
    ): ShoppingListItem {
        val shoppingList = requireFamilyAccess(id)
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
        val shoppingList = requireFamilyAccess(id)
        val removed = shoppingList.items.removeIf { it.id == itemId }
        if (!removed) {
            throw NotFoundException("Shopping list item with ID: $id not found")
        }
        shoppingListRepository.save(shoppingList)
    }

    @Transactional
    fun getShoppingListsByFamily(familyId: UUID): List<ShoppingList> {
        val user = currentUser.user
        familyMemberRepository.findByFamilyIdAndUserCognitoId(familyId, user.cognitoId)
            ?: throw ForbiddenException("You are not a member of this family.")
        return shoppingListRepository.findByFamilyId(familyId).onEach { list ->
            list.items.size
            list.family?.name
        }
    }

    fun createShoppingListFromRecipe(
        name: String,
        link: String,
    ): ShoppingList {
        val shoppingList =
            shoppingListRepository.save(ShoppingList(name, currentUser.user, mutableListOf()))

        val shoppingListFromRecipe = shoppingListFromRecipeGenerator.generateShoppingListFromRecipe(link)

        shoppingListFromRecipe.items.forEach {
            shoppingList.addItem(it.name, it.quantity)
        }

        return shoppingListRepository.save(shoppingList)
    }
}
