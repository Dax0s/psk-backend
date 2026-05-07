package org.kotletai.backend.dto

import org.kotletai.backend.entity.User
import org.kotletai.backend.model.Family
import java.time.Instant
import java.util.UUID

data class FamilyResponse(
    val id: UUID,
    val name: String,
    val inviteCode: String,
    val isAdmin: Boolean,
    val memberCount: Int,
    val createdAt: Instant,
    val members: List<MemberResponse>? = null,
)

fun Family.toResponse(currentUser: User) = FamilyResponse(
    id = id!!,
    name = name,
    inviteCode = inviteCode,
    isAdmin = admin.id == currentUser.id,
    memberCount = members.size,
    createdAt = createdAt,
)

fun Family.toDetailResponse(currentUser: User) = FamilyResponse(
    id = id!!,
    name = name,
    inviteCode = inviteCode,
    isAdmin = admin.id == currentUser.id,
    memberCount = members.size,
    createdAt = createdAt,
    members = members.map { it.toMemberResponse(this) },
)
