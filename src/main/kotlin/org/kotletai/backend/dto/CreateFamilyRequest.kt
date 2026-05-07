package org.kotletai.backend.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateFamilyRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val name: String,
)
