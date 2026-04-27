package org.kotletai.backend.repository

import org.kotletai.backend.model.Family
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FamilyRepository : JpaRepository<Family, UUID> {
    fun findByInviteCode(inviteCode: String): Family?
}
