package org.kotletai.backend.suggestions.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.kotletai.backend.suggestions.domain.PinnedProduct
import org.kotletai.backend.suggestions.domain.SuggestionScope
import org.kotletai.backend.suggestions.domain.SuggestionScopeType
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(
    name = "pinned_products",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_pinned_products_scope_key",
            columnNames = ["scope_type", "scope_id", "product_key"],
        ),
    ],
)
class PinnedProductEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "scope_type", nullable = false, length = 32)
    var scopeType: SuggestionScopeType = SuggestionScopeType.PERSONAL,
    @Column(name = "scope_id", nullable = false, length = 128)
    var scopeId: String = "",
    @Column(name = "product_key", nullable = false, length = 255)
    var productKey: String = "",
    @Column(name = "display_name", nullable = false, length = 255)
    var displayName: String = "",
    @Column(name = "default_quantity", precision = 10, scale = 2)
    var defaultQuantity: BigDecimal? = null,
    @Column(name = "unit", length = 64)
    var unit: String? = null,
    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),
)

fun PinnedProductEntity.toDomain(): PinnedProduct = PinnedProduct(
    id = checkNotNull(id),
    scope = SuggestionScope(type = scopeType, referenceId = scopeId),
    productKey = productKey,
    displayName = displayName,
    defaultQuantity = defaultQuantity,
    unit = unit,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
