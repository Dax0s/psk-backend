package org.kotletai.backend.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "family")
class Family(
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
    @OneToMany(mappedBy = "family", fetch = FetchType.EAGER)
    val shoppingLists: MutableList<ShoppingList> = mutableListOf(),
    @Id
    @Column(nullable = false)
    @GeneratedValue
    val id: UUID? = null,
)
