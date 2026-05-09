package org.kotletai.backend.service

import org.kotletai.backend.config.CurrentUser
import org.kotletai.backend.entity.PinnedProduct
import org.kotletai.backend.entity.ProductCategory
import org.kotletai.backend.entity.User
import org.kotletai.backend.exception.NotFoundException
import org.kotletai.backend.model.SuggestedProductResponse
import org.kotletai.backend.repository.PinnedProductRepository
import org.kotletai.backend.repository.ShoppingListRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.UUID

private const val MINIMUM_SUGGESTION_ENTRY_COUNT = 2

private val whitespaceRegex = Regex("\\s+")

@Service
class SuggestionService(
    private val currentUser: CurrentUser,
    private val shoppingListRepository: ShoppingListRepository,
    private val pinnedProductRepository: PinnedProductRepository,
) {
    fun getSuggestions(): List<SuggestedProductResponse> {
        val user = currentUser.user
        val pinnedKeys =
            pinnedProductRepository
                .findByUserOrderBySortOrderAsc(user)
                .mapTo(mutableSetOf()) { normalizeName(it.name) }

        return shoppingListRepository
            .findByUser(user)
            .flatMap { it.items }
            .filter { it.name.isNotBlank() }
            .groupBy { normalizeName(it.name) }
            .filterKeys { it !in pinnedKeys }
            .filterValues { it.size >= MINIMUM_SUGGESTION_ENTRY_COUNT }
            .map { (_, candidates) ->
                val first = candidates.first()
                SuggestedProductResponse(
                    name = first.name,
                    suggestedQuantity = first.quantity,
                    entryCount = candidates.size,
                    category = first.category,
                )
            }.sortedWith(
                compareByDescending<SuggestedProductResponse> { it.entryCount }
                    .thenBy { it.name.lowercase() },
            )
    }

    fun getPinnedProducts(): List<PinnedProduct> = pinnedProductRepository.findByUserOrderBySortOrderAsc(currentUser.user)

    fun createPinnedProduct(
        name: String,
        defaultQuantity: BigDecimal?,
        sortOrder: Int?,
        category: ProductCategory? = null,
    ): PinnedProduct {
        val user = currentUser.user

        return pinnedProductRepository.save(
            PinnedProduct(
                user,
                name,
                defaultQuantity,
                sortOrder ?: nextSortOrder(user),
                category ?: ProductCategory.OTHER,
            ),
        )
    }

    fun updatePinnedProduct(
        id: UUID,
        name: String,
        defaultQuantity: BigDecimal?,
        sortOrder: Int,
        category: ProductCategory? = null,
    ): PinnedProduct {
        val pinnedProduct =
            pinnedProductRepository.findByIdAndUser(id, currentUser.user)
                ?: throw NotFoundException("Pinned product with ID: $id not found")

        return pinnedProductRepository.save(
            PinnedProduct(
                pinnedProduct.user,
                name,
                defaultQuantity,
                sortOrder,
                category ?: pinnedProduct.category,
                pinnedProduct.id,
            ),
        )
    }

    fun deletePinnedProduct(id: UUID) {
        val pinnedProduct =
            pinnedProductRepository.findByIdAndUser(id, currentUser.user)
                ?: throw NotFoundException("Pinned product with ID: $id not found")

        pinnedProductRepository.delete(pinnedProduct)
    }

    private fun nextSortOrder(user: User): Int =
        pinnedProductRepository
            .findByUserOrderBySortOrderAsc(user)
            .maxOfOrNull(PinnedProduct::sortOrder)
            ?.plus(1)
            ?: 0
}

private fun normalizeName(value: String): String = value.trim().lowercase().replace(whitespaceRegex, " ")
