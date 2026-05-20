package org.kotletai.backend.model

import org.kotletai.backend.entity.FamilyMember
import java.time.Instant
import java.util.UUID

data class MemberResponse(
    val userId: UUID,
    val email: String?,
    val joinedAt: Instant,
    val isAdmin: Boolean,
)

fun FamilyMember.toResponse(adminId: UUID) =
    MemberResponse(
        userId = this.user.cognitoId,
        email = this.user.email,
        joinedAt = this.joinedAt,
        isAdmin = this.user.cognitoId == adminId,
    )
