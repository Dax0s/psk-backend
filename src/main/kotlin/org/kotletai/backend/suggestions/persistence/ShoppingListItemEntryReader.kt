package org.kotletai.backend.suggestions.persistence

import jakarta.persistence.EntityManager
import org.kotletai.backend.suggestions.domain.ItemEntryReader
import org.kotletai.backend.suggestions.domain.ItemEntryRecord
import org.kotletai.backend.suggestions.domain.SuggestionScope
import org.kotletai.backend.suggestions.domain.SuggestionScopeType
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.time.Instant

@Component
@Primary
class ShoppingListItemEntryReader(
    private val entityManager: EntityManager,
) : ItemEntryReader {
    override fun findItemEntries(scope: SuggestionScope): List<ItemEntryRecord> {
        if (scope.type != SuggestionScopeType.PERSONAL) {
            return emptyList()
        }

        return entityManager
            .createQuery(
                """
                select new org.kotletai.backend.suggestions.persistence.ShoppingListItemSuggestionRecord(
                    item.name,
                    item.quantity
                )
                from ShoppingListItem item
                join item.shoppingList shoppingList
                join shoppingList.user owner
                where owner.cognitoId = :cognitoId
                """.trimIndent(),
                ShoppingListItemSuggestionRecord::class.java,
            )
            .setParameter("cognitoId", scope.referenceId)
            .resultList
            .map { record ->
                ItemEntryRecord(
                    displayName = record.name,
                    quantity = record.quantity,
                    enteredAt = Instant.EPOCH,
                )
            }
    }
}
