package org.kotletai.backend.model

import jakarta.validation.constraints.NotBlank

data class ShoppingListFromRecipeRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    @field:NotBlank(message = "Recipe URL is required")
    val link: String,
)
