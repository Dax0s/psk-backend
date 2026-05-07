package org.kotletai.backend.repository

import org.kotletai.backend.entity.User
import org.kotletai.backend.model.Family
import org.kotletai.backend.model.FamilyMember
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FamilyMemberRepository : JpaRepository<FamilyMember, UUID> {
    fun findAllByUser(user: User): List<FamilyMember>
    fun findByFamilyAndUser(family: Family, user: User): FamilyMember?
    fun findByFamilyAndUserCognitoId(family: Family, cognitoId: String): FamilyMember?
}
