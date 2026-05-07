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
    fun createFamily(name: String): Family {
        val inviteCode = generateUniqueInviteCode()
        val user = currentUser.user
        val family = Family(name = name, inviteCode = inviteCode, admin = user)
        family.members.add(FamilyMember(family = family, user = user))
        return familyRepository.save(family).also { it.members.size }
    }

    @Transactional
    fun joinFamily(inviteCode: String): FamilyMember {
        val family = familyRepository.findByInviteCode(inviteCode.trim().uppercase())
            ?: throw NotFoundException("Family not found. Check the invite code and try again.")
        val user = currentUser.user
        if (familyMemberRepository.findByFamilyAndUser(family, user) != null) {
            throw ConflictException("You are already a member of this family.")
        }
        val member = familyMemberRepository.save(FamilyMember(family = family, user = user))
        family.members.size
        return member
    }

    @Transactional
    fun getFamilies(): List<FamilyMember> {
        val memberships = familyMemberRepository.findAllByUser(currentUser.user)
        memberships.forEach { it.family.members.size }
        return memberships
    }

    @Transactional
    fun getFamily(familyId: UUID): Family {
        val family = familyRepository.findById(familyId)
            .orElseThrow { NotFoundException("Family not found.") }
        familyMemberRepository.findByFamilyAndUser(family, currentUser.user)
            ?: throw ForbiddenException("You are not a member of this family.")
        family.members.forEach { it.user.cognitoId; it.user.email }
        return family
    }

    @Transactional
    fun removeMember(familyId: UUID, targetCognitoId: String) {
        val family = familyRepository.findById(familyId)
            .orElseThrow { NotFoundException("Family not found.") }
        if (family.admin.id != currentUser.user.id) {
            throw ForbiddenException("Only the family admin can remove members.")
        }
        if (targetCognitoId == currentUser.cognitoId) {
            throw BadRequestException("Cannot remove the admin from the family.")
        }
        val member = familyMemberRepository.findByFamilyAndUserCognitoId(family, targetCognitoId)
            ?: throw NotFoundException("The specified user is not a member of this family.")
        familyMemberRepository.delete(member)
    }

    @Transactional
    fun leaveFamily(familyId: UUID) {
        val family = familyRepository.findById(familyId)
            .orElseThrow { NotFoundException("Family not found.") }
        val user = currentUser.user
        if (family.admin.id == user.id) {
            throw BadRequestException("Admin cannot leave the family. Delete it instead.")
        }
        val member = familyMemberRepository.findByFamilyAndUser(family, user)
            ?: throw NotFoundException("You are not a member of this family.")
        familyMemberRepository.delete(member)
    }

    @Transactional
    fun deleteFamily(familyId: UUID) {
        val family = familyRepository.findById(familyId)
            .orElseThrow { NotFoundException("Family not found.") }
        if (family.admin.id != currentUser.user.id) {
            throw ForbiddenException("Only the family admin can delete the family.")
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
