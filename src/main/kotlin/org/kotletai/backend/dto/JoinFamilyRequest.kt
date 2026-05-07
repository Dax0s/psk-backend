package org.kotletai.backend.dto

import jakarta.validation.constraints.NotBlank

data class JoinFamilyRequest(
    @field:NotBlank
    val inviteCode: String,
)
