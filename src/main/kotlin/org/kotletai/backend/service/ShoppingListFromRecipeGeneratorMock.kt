package org.kotletai.backend.service

import org.kotletai.backend.model.GeminiShoppingListItem
import org.kotletai.backend.model.ShoppingListFromRecipe
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
@ConditionalOnProperty(value = ["shopping-list-from-recipe-generator"], havingValue = "mock", matchIfMissing = true)
class ShoppingListFromRecipeGeneratorMock : ShoppingListFromRecipeGenerator {

    override fun generateShoppingListFromRecipe(recipeUrl: String): ShoppingListFromRecipe {
        return ShoppingListFromRecipe(
            items = listOf(
                GeminiShoppingListItem(name = "milk (ml)", quantity = BigDecimal(500)),
                GeminiShoppingListItem(name = "lemon", quantity = BigDecimal(2)),
                GeminiShoppingListItem(name = "flour (g)", quantity = BigDecimal(120)),
                GeminiShoppingListItem(name = "olive oil (ml)", quantity = BigDecimal(15)),
                GeminiShoppingListItem(name = "garlic clove", quantity = BigDecimal(3)),
            )
        )
    }
}