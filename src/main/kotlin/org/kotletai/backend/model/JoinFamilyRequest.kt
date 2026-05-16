package org.kotletai.backend.model

import jakarta.validation.constraints.NotBlank

data class JoinFamilyRequest(
    @field:NotBlank(message = "Invite code is required")
    val inviteCode: String,
    val email: String? = null,
)
