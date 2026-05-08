package org.kotletai.backend.service

import org.kotletai.backend.config.CurrentUser
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
class FamilyService(
    private val familyRepository: FamilyRepository,
    private val familyMemberRepository: FamilyMemberRepository,
    private val currentUser: CurrentUser,
) {
    private val inviteCodeChars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"

    @Transactional
    fun createFamily(name: String, email: String?): Family {
        val user = currentUser.user
        if (email != null) user.email = email

        val family = Family(
            name = name,
            inviteCode = generateUniqueInviteCode(),
            admin = user,
        )
        family.members.add(FamilyMember(family = family, user = user))

        return familyRepository.save(family)
    }

    @Transactional
    fun joinFamily(inviteCode: String, email: String?): FamilyMember {
        val user = currentUser.user
        if (email != null) user.email = email
        val family = familyRepository.findByInviteCode(inviteCode.trim().uppercase())
            ?: throw NotFoundException("Family not found. Check the invite code and try again.")

        if (familyMemberRepository.findByFamilyIdAndUserCognitoId(family.id!!, user.cognitoId) != null) {
            throw ConflictException("You are already a member of this family.")
        }

        val member = FamilyMember(family = family, user = user)
        family.members.add(member)

        // Initialize lazy associations before transaction closes
        family.admin.cognitoId
        family.members.size

        return member
    }

    @Transactional
    fun getFamilies(): List<FamilyMember> {
        val user = currentUser.user
        return familyMemberRepository.findAllByUserCognitoId(user.cognitoId).onEach { member ->
            member.family.admin.cognitoId
            member.family.members.size
        }
    }

    @Transactional
    fun getFamily(familyId: UUID): Family {
        val user = currentUser.user
        val family = familyRepository.findById(familyId)
            .orElseThrow { NotFoundException("Family not found.") }

        familyMemberRepository.findByFamilyIdAndUserCognitoId(familyId, user.cognitoId)
            ?: throw ForbiddenException("You are not a member of this family.")

        // Initialize lazy associations before transaction closes
        family.admin.cognitoId
        family.members.forEach { it.user.cognitoId; it.user.email }

        return family
    }

    @Transactional
    fun removeMember(familyId: UUID, targetUserId: String) {
        val user = currentUser.user
        val family = familyRepository.findById(familyId)
            .orElseThrow { NotFoundException("Family not found.") }

        if (family.admin.cognitoId != user.cognitoId) throw ForbiddenException("Only the family admin can remove members.")
        if (targetUserId == family.admin.cognitoId) throw BadRequestException("Cannot remove the admin from the family.")

        familyMemberRepository.findByFamilyIdAndUserCognitoId(familyId, targetUserId)
            ?: throw NotFoundException("The specified user is not a member of this family.")

        familyMemberRepository.deleteByFamilyIdAndUserCognitoId(familyId, targetUserId)
    }

    @Transactional
    fun leaveFamily(familyId: UUID) {
        val user = currentUser.user
        val family = familyRepository.findById(familyId)
            .orElseThrow { NotFoundException("Family not found.") }

        familyMemberRepository.findByFamilyIdAndUserCognitoId(familyId, user.cognitoId)
            ?: throw NotFoundException("You are not a member of this family.")

        if (family.admin.cognitoId == user.cognitoId) throw BadRequestException("Admin cannot leave the family. Delete it instead.")

        familyMemberRepository.deleteByFamilyIdAndUserCognitoId(familyId, user.cognitoId)
    }

    @Transactional
    fun deleteFamily(familyId: UUID) {
        val user = currentUser.user
        val family = familyRepository.findById(familyId)
            .orElseThrow { NotFoundException("Family not found.") }

        if (family.admin.cognitoId != user.cognitoId) throw ForbiddenException("Only the family admin can delete the family.")

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
