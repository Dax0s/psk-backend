package org.kotletai.backend.suggestions.persistence

import org.kotletai.backend.suggestions.domain.SuggestionScopeType
import org.springframework.data.jpa.repository.JpaRepository

interface PinnedProductRepository : JpaRepository<PinnedProductEntity, Long> {
    fun existsByScopeTypeAndScopeIdAndProductKey(
        scopeType: SuggestionScopeType,
        scopeId: String,
        productKey: String,
    ): Boolean

    fun findAllByScopeTypeAndScopeId(scopeType: SuggestionScopeType, scopeId: String): List<PinnedProductEntity>

    fun findAllByScopeTypeAndScopeIdOrderBySortOrderAscDisplayNameAsc(
        scopeType: SuggestionScopeType,
        scopeId: String,
    ): List<PinnedProductEntity>

    fun findByIdAndScopeTypeAndScopeId(
        id: Long,
        scopeType: SuggestionScopeType,
        scopeId: String,
    ): PinnedProductEntity?
}
