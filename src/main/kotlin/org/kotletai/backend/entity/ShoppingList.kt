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
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "shopping_list")
class ShoppingList(
    @Column(nullable = false)
    var name: String,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
    @OneToMany(
        mappedBy = "shoppingList",
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        orphanRemoval = true
    )
    val items: MutableList<ShoppingListItem> = mutableListOf(),
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    val family: Family? = null,
    @Id
    @Column(nullable = false)
    @GeneratedValue
    val id: UUID? = null,
) {
    fun addItem(name: String, quantity: BigDecimal) {
        items.add(ShoppingListItem(this, name, quantity))
    }
}
