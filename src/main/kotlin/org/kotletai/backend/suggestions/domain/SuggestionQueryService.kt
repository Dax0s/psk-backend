package org.kotletai.backend.suggestions.domain

import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Locale

private const val minimumSuggestionEntryCount = 2

@Service
class SuggestionQueryService(
    private val completedPurchaseHistoryReader: CompletedPurchaseHistoryReader,
    private val pinnedProductReader: PinnedProductReader,
) {
    fun getSuggestions(scope: SuggestionScope): List<SuggestedProduct> {
        val pinnedProductKeys = pinnedProductReader.findPinnedProductKeys(scope)

        return completedPurchaseHistoryReader.findCompletedPurchases(scope)
            .mapNotNull(::toPurchaseCandidate)
            .groupBy(PurchaseCandidate::productKey)
            .values
            .map(::toSuggestedProduct)
            .filter { it.purchaseCount >= minimumSuggestionEntryCount }
            .filterNot { it.productKey in pinnedProductKeys }
            .sortedWith(
                compareByDescending<SuggestedProduct> { it.suggestionWeight }
                    .thenBy { it.displayName.lowercase(Locale.ROOT) },
            )
    }

    private fun toPurchaseCandidate(record: CompletedPurchaseRecord): PurchaseCandidate? {
        val displayName = record.displayName.trim()
        if (displayName.isBlank()) {
            return null
        }

        return PurchaseCandidate(
            productKey = record.productKey
                ?.takeIf { it.isNotBlank() }
                ?.let(::normalizeProductKey)
                ?: normalizeProductKey(displayName),
            displayName = displayName,
            quantity = record.quantity,
            unit = record.unit?.trim()?.takeIf { it.isNotBlank() },
            completedAt = record.completedAt,
            signalWeight = record.signalWeight.coerceAtLeast(1),
        )
    }

    private fun toSuggestedProduct(candidates: List<PurchaseCandidate>): SuggestedProduct {
        val mostRecentCandidate = candidates.maxBy(PurchaseCandidate::completedAt)
        val suggestionWeight = candidates.sumOf(PurchaseCandidate::signalWeight)

        return SuggestedProduct(
            productKey = mostRecentCandidate.productKey,
            displayName = mostRecentCandidate.displayName,
            suggestedQuantity = mostRecentCandidate.quantity,
            unit = mostRecentCandidate.unit,
            purchaseCount = candidates.size,
            lastCompletedAt = mostRecentCandidate.completedAt,
            suggestionWeight = suggestionWeight,
        )
    }

    private data class PurchaseCandidate(
        val productKey: String,
        val displayName: String,
        val quantity: java.math.BigDecimal?,
        val unit: String?,
        val completedAt: Instant,
        val signalWeight: Int,
    )
}
