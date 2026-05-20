package org.kotletai.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "\"user\"")
class User(
    @Column(name = "cognito_id", nullable = false)
    val cognitoId: UUID,
    @Column(name = "email")
    var email: String? = null,
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    val shoppingLists: MutableList<ShoppingList> = mutableListOf(),
    @Id
    @Column(nullable = false)
    @GeneratedValue
    val id: UUID? = null,
)
