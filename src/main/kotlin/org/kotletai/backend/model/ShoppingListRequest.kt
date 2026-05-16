package org.kotletai.backend.model

import jakarta.validation.constraints.NotBlank
import java.util.UUID

data class ShoppingListRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    val familyId: UUID? = null,
)
