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
            itemEntryReader = FakeItemEntryReader(
                entries = listOf(
                    ItemEntryRecord(
                        displayName = "Milk",
                        quantity = BigDecimal("2.00"),
                        unit = "l",
                        enteredAt = now.minus(1, ChronoUnit.DAYS),
                    ),
                    ItemEntryRecord(
                        displayName = "Milk",
                        quantity = BigDecimal("1.00"),
                        unit = "l",
                        enteredAt = now.minus(5, ChronoUnit.DAYS),
                    ),
                    ItemEntryRecord(
                        displayName = "Milk",
                        quantity = BigDecimal("1.00"),
                        unit = "l",
                        enteredAt = now.minus(6, ChronoUnit.DAYS),
                    ),
                    ItemEntryRecord(
                        displayName = "Eggs",
                        quantity = BigDecimal("10.00"),
                        unit = "pcs",
                        enteredAt = now.minus(2, ChronoUnit.DAYS),
                    ),
                    ItemEntryRecord(
                        displayName = "Bread",
                        quantity = BigDecimal("1.00"),
                        unit = "loaf",
                        enteredAt = now.minus(3, ChronoUnit.DAYS),
                    ),
                    ItemEntryRecord(
                        displayName = "Bread",
                        quantity = BigDecimal("1.00"),
                        unit = "loaf",
                        enteredAt = now.minus(4, ChronoUnit.DAYS),
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
            itemEntryReader = FakeItemEntryReader(
                entries = listOf(
                    ItemEntryRecord(
                        displayName = "  Green Apples  ",
                        quantity = BigDecimal("4.00"),
                        unit = "pcs",
                        enteredAt = now.minus(10, ChronoUnit.DAYS),
                    ),
                    ItemEntryRecord(
                        displayName = "green   apples",
                        quantity = BigDecimal("6.00"),
                        unit = "pcs",
                        enteredAt = now.minus(1, ChronoUnit.DAYS),
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
        assertEquals(2, suggestions.single().entryCount)
    }

    @Test
    fun `ranks repeated item entries by entry count only`() {
        val now = Instant.now()
        val service = SuggestionQueryService(
            itemEntryReader = FakeItemEntryReader(
                entries = listOf(
                    ItemEntryRecord(
                        displayName = "Milk",
                        quantity = BigDecimal("1.00"),
                        enteredAt = now.minus(1, ChronoUnit.DAYS),
                    ),
                    ItemEntryRecord(
                        displayName = "Milk",
                        quantity = BigDecimal("1.00"),
                        enteredAt = now.minus(2, ChronoUnit.DAYS),
                    ),
                    ItemEntryRecord(
                        displayName = "Bread",
                        quantity = BigDecimal("1.00"),
                        enteredAt = now.minus(40, ChronoUnit.DAYS),
                    ),
                    ItemEntryRecord(
                        displayName = "Bread",
                        quantity = BigDecimal("1.00"),
                        enteredAt = now.minus(41, ChronoUnit.DAYS),
                    ),
                    ItemEntryRecord(
                        displayName = "Bread",
                        quantity = BigDecimal("1.00"),
                        enteredAt = now.minus(42, ChronoUnit.DAYS),
                    ),
                ),
            ),
            pinnedProductReader = FakePinnedProductReader(emptySet()),
        )

        val suggestions = service.getSuggestions(scope)

        assertEquals(listOf("bread", "milk"), suggestions.map(SuggestedProduct::productKey))
        assertEquals(3, suggestions.first().entryCount)
    }

    @Test
    fun `ignores products with fewer than two entries`() {
        val now = Instant.now()
        val service = SuggestionQueryService(
            itemEntryReader = FakeItemEntryReader(
                entries = listOf(
                    ItemEntryRecord(
                        displayName = "Milk",
                        quantity = BigDecimal("1.00"),
                        enteredAt = now.minus(1, ChronoUnit.DAYS),
                    ),
                    ItemEntryRecord(
                        displayName = "Bread",
                        quantity = BigDecimal("1.00"),
                        enteredAt = now.minus(2, ChronoUnit.DAYS),
                    ),
                    ItemEntryRecord(
                        displayName = "Bread",
                        quantity = BigDecimal("1.00"),
                        enteredAt = now.minus(3, ChronoUnit.DAYS),
                    ),
                ),
            ),
            pinnedProductReader = FakePinnedProductReader(emptySet()),
        )

        val suggestions = service.getSuggestions(scope)

        assertEquals(listOf("bread"), suggestions.map(SuggestedProduct::productKey))
    }
}

private class FakeItemEntryReader(
    private val entries: List<ItemEntryRecord>,
) : ItemEntryReader {
    override fun findItemEntries(scope: SuggestionScope): List<ItemEntryRecord> = entries
}

private class FakePinnedProductReader(
    private val pinnedProductKeys: Set<String>,
) : PinnedProductReader {
    override fun findPinnedProductKeys(scope: SuggestionScope): Set<String> = pinnedProductKeys
}
