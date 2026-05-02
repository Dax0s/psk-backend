package org.kotletai.backend.suggestions.domain

interface PinnedProductReader {
    fun findPinnedProductKeys(scope: SuggestionScope): Set<String>
}
