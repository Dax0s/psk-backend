package org.kotletai.backend.model

import org.kotletai.backend.entity.User
import org.kotletai.backend.entity.UserRole
import java.util.UUID

data class CurrentUserResponse(
    val id: UUID,
    val email: String?,
    val role: UserRole,
)

fun User.toCurrentUserResponse() =
    CurrentUserResponse(
        id = requireNotNull(this.id),
        email = this.email,
        role = this.role,
    )
