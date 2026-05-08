package org.kotletai.backend.suggestions.persistence

import org.kotletai.backend.suggestions.domain.PinnedProduct
import org.kotletai.backend.suggestions.domain.SuggestionScope
import org.kotletai.backend.suggestions.domain.normalizeProductKey
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.time.Instant

@Service
@Transactional
class PinnedProductService(
    private val pinnedProductRepository: PinnedProductRepository,
) {
    @Transactional(readOnly = true)
    fun getPinnedProducts(scope: SuggestionScope): List<PinnedProduct> = pinnedProductRepository
        .findAllByScopeTypeAndScopeIdOrderBySortOrderAscDisplayNameAsc(scope.type, scope.referenceId)
        .map(PinnedProductEntity::toDomain)

    fun createPinnedProduct(scope: SuggestionScope, command: UpsertPinnedProductCommand): PinnedProduct {
        val productDraft = command.toDraft()

        if (pinnedProductRepository.existsByScopeTypeAndScopeIdAndProductKey(scope.type, scope.referenceId, productDraft.productKey)) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Pinned product '${productDraft.displayName}' already exists for this scope.",
            )
        }

        val now = Instant.now()
        val entity = pinnedProductRepository.save(
            PinnedProductEntity(
                scopeType = scope.type,
                scopeId = scope.referenceId,
                productKey = productDraft.productKey,
                displayName = productDraft.displayName,
                defaultQuantity = productDraft.defaultQuantity,
                unit = productDraft.unit,
                sortOrder = productDraft.sortOrder ?: nextSortOrder(scope),
                createdAt = now,
                updatedAt = now,
            ),
        )

        return entity.toDomain()
    }

    fun updatePinnedProduct(scope: SuggestionScope, id: Long, command: UpsertPinnedProductCommand): PinnedProduct {
        val entity = pinnedProductRepository.findByIdAndScopeTypeAndScopeId(id, scope.type, scope.referenceId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Pinned product '$id' was not found for this scope.")

        val productDraft = command.toDraft()
        val duplicateExists = pinnedProductRepository.existsByScopeTypeAndScopeIdAndProductKey(
            scope.type,
            scope.referenceId,
            productDraft.productKey,
        ) && entity.productKey != productDraft.productKey

        if (duplicateExists) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Pinned product '${productDraft.displayName}' already exists for this scope.",
            )
        }

        entity.productKey = productDraft.productKey
        entity.displayName = productDraft.displayName
        entity.defaultQuantity = productDraft.defaultQuantity
        entity.unit = productDraft.unit
        productDraft.sortOrder?.let { entity.sortOrder = it }
        entity.updatedAt = Instant.now()

        return pinnedProductRepository.save(entity).toDomain()
    }

    fun deletePinnedProduct(scope: SuggestionScope, id: Long) {
        val entity = pinnedProductRepository.findByIdAndScopeTypeAndScopeId(id, scope.type, scope.referenceId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Pinned product '$id' was not found for this scope.")

        pinnedProductRepository.delete(entity)
    }

    private fun nextSortOrder(scope: SuggestionScope): Int = pinnedProductRepository
        .findAllByScopeTypeAndScopeId(scope.type, scope.referenceId)
        .maxOfOrNull(PinnedProductEntity::sortOrder)
        ?.plus(1)
        ?: 0
}

data class UpsertPinnedProductCommand(
    val displayName: String,
    val productKey: String? = null,
    val defaultQuantity: BigDecimal? = null,
    val unit: String? = null,
    val sortOrder: Int? = null,
) {
    fun toDraft(): PinnedProductDraft {
        val normalizedDisplayName = displayName.trim()
        if (normalizedDisplayName.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Display name must not be blank.")
        }

        return PinnedProductDraft(
            displayName = normalizedDisplayName,
            productKey = productKey
                ?.trim()
                ?.takeIf { it.isNotBlank() }
                ?.let(::normalizeProductKey)
                ?: normalizeProductKey(normalizedDisplayName),
            defaultQuantity = defaultQuantity,
            unit = unit?.trim()?.takeIf { it.isNotBlank() },
            sortOrder = sortOrder,
        )
    }
}

data class PinnedProductDraft(
    val displayName: String,
    val productKey: String,
    val defaultQuantity: BigDecimal?,
    val unit: String?,
    val sortOrder: Int?,
)
