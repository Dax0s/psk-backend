package org.kotletai.backend.model

import jakarta.validation.constraints.NotBlank

data class ShoppingListRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
)
