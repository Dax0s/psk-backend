package org.kotletai.backend.service

import org.kotletai.backend.dto.FamilyDetailResponse
import org.kotletai.backend.dto.FamilyResponse
import org.kotletai.backend.dto.FamilySummaryResponse
import org.kotletai.backend.dto.MemberResponse
import org.kotletai.backend.exception.BadRequestException
import org.kotletai.backend.exception.ConflictException
import org.kotletai.backend.exception.ForbiddenException
import org.kotletai.backend.exception.NotFoundException
import org.kotletai.backend.model.Family
import org.kotletai.backend.model.FamilyMember
import org.kotletai.backend.repository.FamilyMemberRepository
import org.kotletai.backend.repository.FamilyRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class FamilyService(
    private val familyRepository: FamilyRepository,
    private val familyMemberRepository: FamilyMemberRepository,
) {
    private val inviteCodeChars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"

    fun createFamily(userId: String, name: String, email: String?): FamilyResponse {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) throw BadRequestException("INVALID_NAME", "Family name must not be blank.")
        if (trimmedName.length > 100) throw BadRequestException("INVALID_NAME", "Family name must be at most 100 characters.")

        val inviteCode = generateUniqueInviteCode()

        val family = familyRepository.save(
            Family(
                name = trimmedName,
                inviteCode = inviteCode,
                adminId = userId,
            ),
        )

        familyMemberRepository.save(
            FamilyMember(
                familyId = family.id!!,
                userId = userId,
                email = email,
            ),
        )

        return FamilyResponse(
            id = family.id!!,
            name = family.name,
            inviteCode = family.inviteCode,
            isAdmin = true,
            memberCount = 1,
            createdAt = family.createdAt,
        )
    }

    fun joinFamily(userId: String, inviteCode: String, email: String?): FamilyResponse {
        val family = familyRepository.findByInviteCode(inviteCode.trim().uppercase())
            ?: throw NotFoundException("FAMILY_NOT_FOUND", "Family not found. Check the invite code and try again.")

        val familyId = family.id!!

        if (familyMemberRepository.findByFamilyIdAndUserId(familyId, userId) != null) {
            throw ConflictException("ALREADY_MEMBER", "You are already a member of this family.")
        }

        familyMemberRepository.save(
            FamilyMember(
                familyId = familyId,
                userId = userId,
                email = email,
            ),
        )

        val memberCount = familyMemberRepository.countByFamilyId(familyId)

        return FamilyResponse(
            id = familyId,
            name = family.name,
            inviteCode = family.inviteCode,
            isAdmin = family.adminId == userId,
            memberCount = memberCount.toInt(),
            createdAt = family.createdAt,
        )
    }

    @Transactional(readOnly = true)
    fun getMyFamilies(userId: String): List<FamilySummaryResponse> {
        val memberships = familyMemberRepository.findAllByUserId(userId)
        val familyIds = memberships.map { it.familyId }.toSet()
        val families = familyRepository.findAllById(familyIds).associateBy { it.id!! }
        val memberCounts = familyIds.associateWith { familyMemberRepository.countByFamilyId(it) }

        return memberships.mapNotNull { membership ->
            val family = families[membership.familyId] ?: return@mapNotNull null
            FamilySummaryResponse(
                id = family.id!!,
                name = family.name,
                inviteCode = family.inviteCode,
                isAdmin = family.adminId == userId,
                memberCount = (memberCounts[family.id!!] ?: 0L).toInt(),
            )
        }
    }

    @Transactional(readOnly = true)
    fun getFamilyDetails(userId: String, familyId: UUID): FamilyDetailResponse {
        val family = familyRepository.findById(familyId)
            .orElseThrow { NotFoundException("FAMILY_NOT_FOUND", "Family not found.") }

        familyMemberRepository.findByFamilyIdAndUserId(familyId, userId)
            ?: throw ForbiddenException("NOT_A_MEMBER", "You are not a member of this family.")

        val members = familyMemberRepository.findAllByFamilyId(familyId).map { member ->
            MemberResponse(
                userId = member.userId,
                email = member.email,
                joinedAt = member.joinedAt,
                isAdmin = member.userId == family.adminId,
            )
        }

        return FamilyDetailResponse(
            id = family.id!!,
            name = family.name,
            inviteCode = family.inviteCode,
            isAdmin = family.adminId == userId,
            createdAt = family.createdAt,
            members = members,
        )
    }

    fun removeMember(adminId: String, familyId: UUID, targetUserId: String) {
        val family = familyRepository.findById(familyId)
            .orElseThrow { NotFoundException("FAMILY_NOT_FOUND", "Family not found.") }

        if (family.adminId != adminId) {
            throw ForbiddenException("NOT_ADMIN", "Only the family admin can remove members.")
        }
        if (targetUserId == family.adminId) {
            throw BadRequestException("CANNOT_REMOVE_ADMIN", "Cannot remove the admin from the family.")
        }

        familyMemberRepository.findByFamilyIdAndUserId(familyId, targetUserId)
            ?: throw NotFoundException("MEMBER_NOT_FOUND", "The specified user is not a member of this family.")

        familyMemberRepository.deleteByFamilyIdAndUserId(familyId, targetUserId)
    }

    fun leaveFamily(userId: String, familyId: UUID) {
        val family = familyRepository.findById(familyId)
            .orElseThrow { NotFoundException("FAMILY_NOT_FOUND", "Family not found.") }

        familyMemberRepository.findByFamilyIdAndUserId(familyId, userId)
            ?: throw NotFoundException("NOT_A_MEMBER", "You are not a member of this family.")

        if (family.adminId == userId) {
            throw BadRequestException("ADMIN_CANNOT_LEAVE", "Admin cannot leave the family. Delete it instead.")
        }

        familyMemberRepository.deleteByFamilyIdAndUserId(familyId, userId)
    }

    fun deleteFamily(userId: String, familyId: UUID) {
        val family = familyRepository.findById(familyId)
            .orElseThrow { NotFoundException("FAMILY_NOT_FOUND", "Family not found.") }

        if (family.adminId != userId) {
            throw ForbiddenException("NOT_ADMIN", "Only the family admin can delete the family.")
        }

        familyRepository.delete(family)
    }

    private fun generateUniqueInviteCode(): String {
        repeat(10) {
            val code = (1..6).map { inviteCodeChars.random() }.joinToString("")
            if (familyRepository.findByInviteCode(code) == null) return code
        }
        throw IllegalStateException("Failed to generate a unique invite code after 10 attempts.")
    }
}
