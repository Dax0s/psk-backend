package org.kotletai.backend.service

import org.kotletai.backend.model.ShoppingListFromRecipe

interface ShoppingListFromRecipeGenerator {
    fun generateShoppingListFromRecipe(recipeUrl: String): ShoppingListFromRecipe
}