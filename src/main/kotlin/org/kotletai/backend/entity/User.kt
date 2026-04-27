package org.kotletai.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "\"user\"")
class User(
    @Column(name = "cognito_id", nullable = false)
    val cognitoId: String,
    @Id
    @Column(nullable = false)
    @GeneratedValue
    val id: UUID? = null,
)
