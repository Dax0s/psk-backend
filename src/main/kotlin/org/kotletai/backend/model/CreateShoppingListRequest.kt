package org.kotletai.backend.model

import jakarta.validation.constraints.NotBlank

data class CreateShoppingListRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
)
