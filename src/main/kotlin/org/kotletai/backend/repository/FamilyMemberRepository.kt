package org.kotletai.backend.repository

import org.kotletai.backend.model.FamilyMember
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FamilyMemberRepository : JpaRepository<FamilyMember, UUID> {
    fun findAllByUserId(userId: String): List<FamilyMember>
    fun findByFamilyIdAndUserId(familyId: UUID, userId: String): FamilyMember?
    fun findAllByFamilyId(familyId: UUID): List<FamilyMember>
    fun deleteByFamilyIdAndUserId(familyId: UUID, userId: String)
    fun countByFamilyId(familyId: UUID): Long
}
