package org.kotletai.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "pinned_product")
class PinnedProduct(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
    @Column(nullable = false)
    val name: String,
    @Column(name = "default_quantity")
    val defaultQuantity: BigDecimal? = null,
    @Column(name = "sort_order", nullable = false)
    val sortOrder: Int,
    @Id
    @Column(nullable = false)
    @GeneratedValue
    val id: UUID? = null,
)
