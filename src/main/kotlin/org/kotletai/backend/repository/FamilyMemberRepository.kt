package org.kotletai.backend.repository

import org.kotletai.backend.entity.FamilyMember
import org.kotletai.backend.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FamilyMemberRepository : JpaRepository<FamilyMember, UUID> {
    fun findAllByUser(user: User): List<FamilyMember>

    fun findByFamilyIdAndUserCognitoId(
        familyId: UUID,
        cognitoId: String,
    ): FamilyMember?

    fun deleteByFamilyIdAndUserCognitoId(
        familyId: UUID,
        cognitoId: String,
    )
}
