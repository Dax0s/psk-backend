package org.kotletai.backend.model

import java.time.Instant
import java.util.UUID

data class FamilyResponse(
    val id: UUID,
    val name: String,
    val inviteCode: String,
    val isAdmin: Boolean,
    val createdAt: Instant,
    val memberCount: Int,
    val members: List<MemberResponse>? = null,
)

fun Family.toResponse(userId: String, includeMembers: Boolean = false) =
    FamilyResponse(
        id = id!!,
        name = name,
        inviteCode = inviteCode,
        isAdmin = admin.cognitoId == userId,
        createdAt = createdAt,
        memberCount = members.size,
        members = if (includeMembers) members.map { it.toResponse(admin.cognitoId) } else null,
    )
