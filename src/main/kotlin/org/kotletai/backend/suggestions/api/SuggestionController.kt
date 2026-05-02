package org.kotletai.backend.suggestions.api

import org.kotletai.backend.suggestions.domain.PinnedProduct
import org.kotletai.backend.suggestions.domain.SuggestedProduct
import org.kotletai.backend.suggestions.domain.SuggestionQueryService
import org.kotletai.backend.suggestions.domain.SuggestionScope
import org.kotletai.backend.suggestions.domain.SuggestionScopeType
import org.kotletai.backend.suggestions.persistence.PinnedProductService
import org.kotletai.backend.suggestions.persistence.UpsertPinnedProductCommand
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.time.Instant

@RestController
@RequestMapping("/api/v1/suggestion-scopes/{scopeType}/{scopeReferenceId}")
class SuggestionController(
    private val suggestionQueryService: SuggestionQueryService,
    private val pinnedProductService: PinnedProductService,
) {
    @GetMapping("/suggestions")
    fun getSuggestions(
        @PathVariable scopeType: SuggestionScopeType,
        @PathVariable scopeReferenceId: String,
    ): List<SuggestedProductResponse> = suggestionQueryService
        .getSuggestions(scope = SuggestionScope(type = scopeType, referenceId = scopeReferenceId))
        .map(::SuggestedProductResponse)

    @GetMapping("/pinned-products")
    fun getPinnedProducts(
        @PathVariable scopeType: SuggestionScopeType,
        @PathVariable scopeReferenceId: String,
    ): List<PinnedProductResponse> = pinnedProductService
        .getPinnedProducts(scope = SuggestionScope(type = scopeType, referenceId = scopeReferenceId))
        .map(::PinnedProductResponse)

    @PostMapping("/pinned-products")
    @ResponseStatus(HttpStatus.CREATED)
    fun createPinnedProduct(
        @PathVariable scopeType: SuggestionScopeType,
        @PathVariable scopeReferenceId: String,
        @RequestBody request: UpsertPinnedProductRequest,
    ): PinnedProductResponse = PinnedProductResponse(
        pinnedProductService.createPinnedProduct(
            scope = SuggestionScope(type = scopeType, referenceId = scopeReferenceId),
            command = request.toCommand(),
        ),
    )

    @PutMapping("/pinned-products/{pinnedProductId}")
    fun updatePinnedProduct(
        @PathVariable scopeType: SuggestionScopeType,
        @PathVariable scopeReferenceId: String,
        @PathVariable pinnedProductId: Long,
        @RequestBody request: UpsertPinnedProductRequest,
    ): PinnedProductResponse = PinnedProductResponse(
        pinnedProductService.updatePinnedProduct(
            scope = SuggestionScope(type = scopeType, referenceId = scopeReferenceId),
            id = pinnedProductId,
            command = request.toCommand(),
        ),
    )

    @DeleteMapping("/pinned-products/{pinnedProductId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePinnedProduct(
        @PathVariable scopeType: SuggestionScopeType,
        @PathVariable scopeReferenceId: String,
        @PathVariable pinnedProductId: Long,
    ) {
        pinnedProductService.deletePinnedProduct(
            scope = SuggestionScope(type = scopeType, referenceId = scopeReferenceId),
            id = pinnedProductId,
        )
    }
}

data class SuggestedProductResponse(
    val productKey: String,
    val displayName: String,
    val suggestedQuantity: BigDecimal?,
    val unit: String?,
    val purchaseCount: Int,
    val lastCompletedAt: Instant,
) {
    constructor(product: SuggestedProduct) : this(
        productKey = product.productKey,
        displayName = product.displayName,
        suggestedQuantity = product.suggestedQuantity,
        unit = product.unit,
        purchaseCount = product.purchaseCount,
        lastCompletedAt = product.lastCompletedAt,
    )
}

data class PinnedProductResponse(
    val id: Long,
    val scopeType: SuggestionScopeType,
    val scopeReferenceId: String,
    val productKey: String,
    val displayName: String,
    val defaultQuantity: BigDecimal?,
    val unit: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    constructor(product: PinnedProduct) : this(
        id = product.id,
        scopeType = product.scope.type,
        scopeReferenceId = product.scope.referenceId,
        productKey = product.productKey,
        displayName = product.displayName,
        defaultQuantity = product.defaultQuantity,
        unit = product.unit,
        createdAt = product.createdAt,
        updatedAt = product.updatedAt,
    )
}

data class UpsertPinnedProductRequest(
    val displayName: String,
    val productKey: String? = null,
    val defaultQuantity: BigDecimal? = null,
    val unit: String? = null,
) {
    fun toCommand(): UpsertPinnedProductCommand = UpsertPinnedProductCommand(
        displayName = displayName,
        productKey = productKey,
        defaultQuantity = defaultQuantity,
        unit = unit,
    )
}
