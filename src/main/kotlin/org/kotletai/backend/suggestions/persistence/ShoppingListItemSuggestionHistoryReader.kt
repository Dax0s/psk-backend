package org.kotletai.backend.suggestions.persistence

import jakarta.persistence.EntityManager
import org.kotletai.backend.suggestions.domain.CompletedPurchaseHistoryReader
import org.kotletai.backend.suggestions.domain.CompletedPurchaseRecord
import org.kotletai.backend.suggestions.domain.SuggestionScope
import org.kotletai.backend.suggestions.domain.SuggestionScopeType
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.time.Instant

private const val enteredItemWeight = 1
private const val checkedItemWeight = 2

@Component
@Primary
class ShoppingListItemSuggestionHistoryReader(
    private val entityManager: EntityManager,
) : CompletedPurchaseHistoryReader {
    override fun findCompletedPurchases(scope: SuggestionScope): List<CompletedPurchaseRecord> {
        if (scope.type != SuggestionScopeType.PERSONAL) {
            return emptyList()
        }

        return entityManager
            .createQuery(
                """
                select new org.kotletai.backend.suggestions.persistence.ShoppingListItemSuggestionRecord(
                    item.name,
                    item.quantity,
                    item.checked
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
                CompletedPurchaseRecord(
                    displayName = record.name,
                    quantity = record.quantity,
                    completedAt = Instant.EPOCH,
                    signalWeight = if (record.checked) checkedItemWeight else enteredItemWeight,
                )
            }
    }
}
