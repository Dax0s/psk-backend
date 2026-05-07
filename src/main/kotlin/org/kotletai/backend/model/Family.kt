package org.kotletai.backend.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.kotletai.backend.entity.User
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "families")
data class Family(
    @Column(nullable = false, length = 100)
    val name: String,

    @Column(name = "invite_code", nullable = false, length = 6, unique = true)
    val inviteCode: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admin_id", nullable = false)
    val admin: User,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @OneToMany(mappedBy = "family", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val members: MutableList<FamilyMember> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
)
