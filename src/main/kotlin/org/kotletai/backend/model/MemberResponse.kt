package org.kotletai.backend.model

import java.time.Instant

data class MemberResponse(
    val userId: String,
    val email: String?,
    val joinedAt: Instant,
    val isAdmin: Boolean,
)

fun FamilyMember.toResponse(adminId: String) =
    MemberResponse(
        userId = this.user.cognitoId,
        email = this.user.email,
        joinedAt = this.joinedAt,
        isAdmin = this.user.cognitoId == adminId,
    )
