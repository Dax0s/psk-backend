package org.kotletai.backend.model

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.kotletai.backend.entity.ProductCategory
import java.math.BigDecimal

data class UpdateShoppingListItemRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    @field:Min(value = 0, message = "Quantity should be more than or equal to 0")
    val quantity: BigDecimal,
    @field:NotNull(message = "Checked is required")
    val checked: Boolean,
    @field:Min(value = 0, message = "Version should be more than or equal to 0")
    val version: Int,
    val category: ProductCategory? = null,
)
