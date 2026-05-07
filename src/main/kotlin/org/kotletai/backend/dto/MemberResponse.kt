package org.kotletai.backend.dto

import org.kotletai.backend.model.Family
import org.kotletai.backend.model.FamilyMember
import java.time.Instant

data class MemberResponse(
    val userId: String,
    val email: String?,
    val joinedAt: Instant,
    val isAdmin: Boolean,
)

fun FamilyMember.toMemberResponse(family: Family) = MemberResponse(
    userId = user.cognitoId,
    email = user.email,
    joinedAt = joinedAt,
    isAdmin = user.id == family.admin.id,
)
