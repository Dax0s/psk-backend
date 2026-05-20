package org.kotletai.backend.repository

import org.kotletai.backend.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
    fun findByCognitoId(cognitoId: UUID): User?
}
