package org.kotletai.backend.suggestions.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit

class SuggestionQueryServiceTest {
    private val scope = SuggestionScope(type = SuggestionScopeType.PERSONAL, referenceId = "user-1")

    @Test
    fun `returns ranked suggestions and excludes pinned products`() {
        val now = Instant.now()
        val service = SuggestionQueryService(
            completedPurchaseHistoryReader = FakeCompletedPurchaseHistoryReader(
                purchases = listOf(
                    CompletedPurchaseRecord(
                        displayName = "Milk",
                        quantity = BigDecimal("2.00"),
                        unit = "l",
                        completedAt = now.minus(1, ChronoUnit.DAYS),
                    ),
                    CompletedPurchaseRecord(
                        displayName = "Milk",
                        quantity = BigDecimal("1.00"),
                        unit = "l",
                        completedAt = now.minus(5, ChronoUnit.DAYS),
                    ),
                    CompletedPurchaseRecord(
                        displayName = "Milk",
                        quantity = BigDecimal("1.00"),
                        unit = "l",
                        completedAt = now.minus(6, ChronoUnit.DAYS),
                    ),
                    CompletedPurchaseRecord(
                        displayName = "Eggs",
                        quantity = BigDecimal("10.00"),
                        unit = "pcs",
                        completedAt = now.minus(2, ChronoUnit.DAYS),
                    ),
                    CompletedPurchaseRecord(
                        displayName = "Bread",
                        quantity = BigDecimal("1.00"),
                        unit = "loaf",
                        completedAt = now.minus(3, ChronoUnit.DAYS),
                    ),
                    CompletedPurchaseRecord(
                        displayName = "Bread",
                        quantity = BigDecimal("1.00"),
                        unit = "loaf",
                        completedAt = now.minus(4, ChronoUnit.DAYS),
                    ),
                ),
            ),
            pinnedProductReader = FakePinnedProductReader(setOf(normalizeProductKey("Eggs"))),
        )

        val suggestions = service.getSuggestions(scope)

        assertEquals(listOf("milk", "bread"), suggestions.map(SuggestedProduct::productKey))
        assertEquals(BigDecimal("2.00"), suggestions.first().suggestedQuantity)
    }

    @Test
    fun `groups records by normalized key and keeps the most recent defaults`() {
        val now = Instant.now()
        val service = SuggestionQueryService(
            completedPurchaseHistoryReader = FakeCompletedPurchaseHistoryReader(
                purchases = listOf(
                    CompletedPurchaseRecord(
                        displayName = "  Green Apples  ",
                        quantity = BigDecimal("4.00"),
                        unit = "pcs",
                        completedAt = now.minus(10, ChronoUnit.DAYS),
                    ),
                    CompletedPurchaseRecord(
                        displayName = "green   apples",
                        quantity = BigDecimal("6.00"),
                        unit = "pcs",
                        completedAt = now.minus(1, ChronoUnit.DAYS),
                    ),
                ),
            ),
            pinnedProductReader = FakePinnedProductReader(emptySet()),
        )

        val suggestions = service.getSuggestions(scope)

        assertEquals(1, suggestions.size)
        assertEquals("green apples", suggestions.single().productKey)
        assertEquals("green   apples", suggestions.single().displayName)
        assertEquals(BigDecimal("6.00"), suggestions.single().suggestedQuantity)
        assertEquals(2, suggestions.single().purchaseCount)
    }

    @Test
    fun `ranks stronger completed signals ahead of weaker entered signals`() {
        val now = Instant.now()
        val service = SuggestionQueryService(
            completedPurchaseHistoryReader = FakeCompletedPurchaseHistoryReader(
                purchases = listOf(
                    CompletedPurchaseRecord(
                        displayName = "Milk",
                        quantity = BigDecimal("1.00"),
                        completedAt = now.minus(1, ChronoUnit.DAYS),
                    ),
                    CompletedPurchaseRecord(
                        displayName = "Milk",
                        quantity = BigDecimal("1.00"),
                        completedAt = now.minus(2, ChronoUnit.DAYS),
                    ),
                    CompletedPurchaseRecord(
                        displayName = "Bread",
                        quantity = BigDecimal("1.00"),
                        completedAt = now.minus(3, ChronoUnit.DAYS),
                        signalWeight = 2,
                    ),
                    CompletedPurchaseRecord(
                        displayName = "Bread",
                        quantity = BigDecimal("1.00"),
                        completedAt = now.minus(4, ChronoUnit.DAYS),
                    ),
                ),
            ),
            pinnedProductReader = FakePinnedProductReader(emptySet()),
        )

        val suggestions = service.getSuggestions(scope)

        assertEquals(listOf("bread", "milk"), suggestions.map(SuggestedProduct::productKey))
        assertEquals(2, suggestions.first().purchaseCount)
        assertEquals(3, suggestions.first().suggestionWeight)
    }

    @Test
    fun `does not boost recent products when ranking suggestions`() {
        val now = Instant.now()
        val service = SuggestionQueryService(
            completedPurchaseHistoryReader = FakeCompletedPurchaseHistoryReader(
                purchases = listOf(
                    CompletedPurchaseRecord(
                        displayName = "Milk",
                        quantity = BigDecimal("1.00"),
                        completedAt = now.minus(1, ChronoUnit.DAYS),
                    ),
                    CompletedPurchaseRecord(
                        displayName = "Milk",
                        quantity = BigDecimal("1.00"),
                        completedAt = now.minus(2, ChronoUnit.DAYS),
                    ),
                    CompletedPurchaseRecord(
                        displayName = "Bread",
                        quantity = BigDecimal("1.00"),
                        completedAt = now.minus(40, ChronoUnit.DAYS),
                    ),
                    CompletedPurchaseRecord(
                        displayName = "Bread",
                        quantity = BigDecimal("1.00"),
                        completedAt = now.minus(41, ChronoUnit.DAYS),
                    ),
                ),
            ),
            pinnedProductReader = FakePinnedProductReader(emptySet()),
        )

        val suggestions = service.getSuggestions(scope)

        assertEquals(listOf("bread", "milk"), suggestions.map(SuggestedProduct::productKey))
    }

    @Test
    fun `ignores products with fewer than two entries`() {
        val now = Instant.now()
        val service = SuggestionQueryService(
            completedPurchaseHistoryReader = FakeCompletedPurchaseHistoryReader(
                purchases = listOf(
                    CompletedPurchaseRecord(
                        displayName = "Milk",
                        quantity = BigDecimal("1.00"),
                        completedAt = now.minus(1, ChronoUnit.DAYS),
                    ),
                    CompletedPurchaseRecord(
                        displayName = "Bread",
                        quantity = BigDecimal("1.00"),
                        completedAt = now.minus(2, ChronoUnit.DAYS),
                    ),
                    CompletedPurchaseRecord(
                        displayName = "Bread",
                        quantity = BigDecimal("1.00"),
                        completedAt = now.minus(3, ChronoUnit.DAYS),
                    ),
                ),
            ),
            pinnedProductReader = FakePinnedProductReader(emptySet()),
        )

        val suggestions = service.getSuggestions(scope)

        assertEquals(listOf("bread"), suggestions.map(SuggestedProduct::productKey))
    }
}

private class FakeCompletedPurchaseHistoryReader(
    private val purchases: List<CompletedPurchaseRecord>,
) : CompletedPurchaseHistoryReader {
    override fun findCompletedPurchases(scope: SuggestionScope): List<CompletedPurchaseRecord> = purchases
}

private class FakePinnedProductReader(
    private val pinnedProductKeys: Set<String>,
) : PinnedProductReader {
    override fun findPinnedProductKeys(scope: SuggestionScope): Set<String> = pinnedProductKeys
}
