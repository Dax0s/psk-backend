package org.kotletai.backend.suggestions.domain

interface CompletedPurchaseHistoryReader {
    fun findCompletedPurchases(scope: SuggestionScope): List<CompletedPurchaseRecord>
}
