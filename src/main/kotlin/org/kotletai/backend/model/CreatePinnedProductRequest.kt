package org.kotletai.backend.model

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal

data class CreatePinnedProductRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    @field:Min(value = 0, message = "Default quantity should be more than or equal to 0")
    val defaultQuantity: BigDecimal? = null,
    val sortOrder: Int? = null,
)
