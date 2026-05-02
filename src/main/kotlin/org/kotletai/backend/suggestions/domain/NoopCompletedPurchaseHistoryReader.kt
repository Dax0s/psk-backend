package org.kotletai.backend.suggestions.domain

import org.springframework.stereotype.Component

@Component
class NoopCompletedPurchaseHistoryReader : CompletedPurchaseHistoryReader {
    override fun findCompletedPurchases(scope: SuggestionScope): List<CompletedPurchaseRecord> = emptyList()
}
