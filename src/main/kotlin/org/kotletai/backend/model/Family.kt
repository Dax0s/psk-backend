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
@Table(name = "families")
data class Family(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false, length = 100)
    val name: String,

    @Column(name = "invite_code", nullable = false, length = 6, unique = true)
    val inviteCode: String,

    @Column(name = "admin_id", nullable = false)
    val adminId: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
)
