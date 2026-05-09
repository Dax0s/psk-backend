package org.kotletai.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "shopping_list_item")
class ShoppingListItem(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shopping_list_id", nullable = false)
    val shoppingList: ShoppingList,
    @Column(nullable = false)
    val name: String,
    @Column(nullable = false)
    val quantity: BigDecimal,
    @Column(nullable = false)
    val checked: Boolean = false,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val category: ProductCategory = ProductCategory.OTHER,
    @Id
    @Column(nullable = false)
    @GeneratedValue
    val id: UUID? = null,
)
