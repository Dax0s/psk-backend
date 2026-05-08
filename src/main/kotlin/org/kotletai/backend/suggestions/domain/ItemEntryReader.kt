package org.kotletai.backend.suggestions.domain

interface ItemEntryReader {
    fun findItemEntries(scope: SuggestionScope): List<ItemEntryRecord>
}
