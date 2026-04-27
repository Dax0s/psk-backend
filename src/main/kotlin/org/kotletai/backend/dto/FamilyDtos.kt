package org.kotletai.backend.dto

import java.time.Instant
import java.util.UUID

// --- Request bodies ---

data class CreateFamilyRequest(val name: String, val email: String? = null)

data class JoinFamilyRequest(val inviteCode: String, val email: String? = null)

// --- Response bodies ---

data class FamilyResponse(
    val id: UUID,
    val name: String,
    val inviteCode: String,
    val isAdmin: Boolean,
    val memberCount: Int,
    val createdAt: Instant,
)

data class FamilySummaryResponse(
    val id: UUID,
    val name: String,
    val inviteCode: String,
    val isAdmin: Boolean,
    val memberCount: Int,
)

data class FamilyDetailResponse(
    val id: UUID,
    val name: String,
    val inviteCode: String,
    val isAdmin: Boolean,
    val createdAt: Instant,
    val members: List<MemberResponse>,
)

data class MemberResponse(
    val userId: String,
    val email: String?,
    val joinedAt: Instant,
    val isAdmin: Boolean,
)

// --- Error response ---

data class ErrorResponse(
    val error: String,
    val message: String,
)
