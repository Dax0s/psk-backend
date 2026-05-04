package org.kotletai.backend.model

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal

data class CreateShoppingListItemRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    @field:Min(value = 0, message = "Quantity should be more than or equal to 0")
    val quantity: BigDecimal,
)
