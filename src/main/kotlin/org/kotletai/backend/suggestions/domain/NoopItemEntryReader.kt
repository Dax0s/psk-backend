package org.kotletai.backend.suggestions.domain

import org.springframework.stereotype.Component

@Component
class NoopItemEntryReader : ItemEntryReader {
    override fun findItemEntries(scope: SuggestionScope): List<ItemEntryRecord> = emptyList()
}
