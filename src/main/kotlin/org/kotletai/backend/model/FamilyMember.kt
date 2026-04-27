package org.kotletai.backend.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "family_members")
data class FamilyMember(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "family_id", nullable = false)
    val familyId: UUID,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(name = "email")
    val email: String? = null,

    @Column(name = "joined_at", nullable = false)
    val joinedAt: Instant = Instant.now(),
)
