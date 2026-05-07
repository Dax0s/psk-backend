package org.kotletai.backend.repository

import org.kotletai.backend.model.FamilyMember
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FamilyMemberRepository : JpaRepository<FamilyMember, UUID> {
    fun findAllByUserCognitoId(cognitoId: String): List<FamilyMember>
    fun findByFamilyIdAndUserCognitoId(familyId: UUID, cognitoId: String): FamilyMember?
    fun deleteByFamilyIdAndUserCognitoId(familyId: UUID, cognitoId: String)
}
