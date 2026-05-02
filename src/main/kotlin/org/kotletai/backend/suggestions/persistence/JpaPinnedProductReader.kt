package org.kotletai.backend.suggestions.persistence

import org.kotletai.backend.suggestions.domain.PinnedProductReader
import org.kotletai.backend.suggestions.domain.SuggestionScope
import org.springframework.stereotype.Component

@Component
class JpaPinnedProductReader(
    private val pinnedProductRepository: PinnedProductRepository,
) : PinnedProductReader {
    override fun findPinnedProductKeys(scope: SuggestionScope): Set<String> = pinnedProductRepository
        .findAllByScopeTypeAndScopeId(scope.type, scope.referenceId)
        .mapTo(linkedSetOf()) { it.productKey }
}
