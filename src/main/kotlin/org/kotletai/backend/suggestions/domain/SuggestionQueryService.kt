package org.kotletai.backend.suggestions.domain

import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Locale

private const val minimumSuggestionEntryCount = 2

@Service
class SuggestionQueryService(
    private val itemEntryReader: ItemEntryReader,
    private val pinnedProductReader: PinnedProductReader,
) {
    fun getSuggestions(scope: SuggestionScope): List<SuggestedProduct> {
        val pinnedProductKeys = pinnedProductReader.findPinnedProductKeys(scope)

        return itemEntryReader.findItemEntries(scope)
            .mapNotNull(::toItemCandidate)
            .groupBy(ItemCandidate::productKey)
            .values
            .map(::toSuggestedProduct)
            .filter { it.entryCount >= minimumSuggestionEntryCount }
            .filterNot { it.productKey in pinnedProductKeys }
            .sortedWith(
                compareByDescending<SuggestedProduct> { it.entryCount }
                    .thenBy { it.displayName.lowercase(Locale.ROOT) },
            )
    }

    private fun toItemCandidate(record: ItemEntryRecord): ItemCandidate? {
        val displayName = record.displayName.trim()
        if (displayName.isBlank()) {
            return null
        }

        return ItemCandidate(
            productKey = record.productKey
                ?.takeIf { it.isNotBlank() }
                ?.let(::normalizeProductKey)
                ?: normalizeProductKey(displayName),
            displayName = displayName,
            quantity = record.quantity,
            unit = record.unit?.trim()?.takeIf { it.isNotBlank() },
            enteredAt = record.enteredAt,
        )
    }

    private fun toSuggestedProduct(candidates: List<ItemCandidate>): SuggestedProduct {
        val mostRecentCandidate = candidates.maxBy(ItemCandidate::enteredAt)

        return SuggestedProduct(
            productKey = mostRecentCandidate.productKey,
            displayName = mostRecentCandidate.displayName,
            suggestedQuantity = mostRecentCandidate.quantity,
            unit = mostRecentCandidate.unit,
            entryCount = candidates.size,
            lastEnteredAt = mostRecentCandidate.enteredAt,
        )
    }

    private data class ItemCandidate(
        val productKey: String,
        val displayName: String,
        val quantity: java.math.BigDecimal?,
        val unit: String?,
        val enteredAt: Instant,
    )
}
