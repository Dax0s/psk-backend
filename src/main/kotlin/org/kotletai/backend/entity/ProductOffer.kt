package org.kotletai.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "product_offer")
class ProductOffer(
    @Column(nullable = false)
    val source: String,
    @Column(nullable = false)
    val category: String,
    @Column(nullable = false)
    val name: String,
    @Column(precision = 10, scale = 2)
    val price: BigDecimal?,
    @Column(name = "old_price")
    val oldPrice: String?,
    @Column(name = "discount_pct")
    val discountPct: Int?,
    @Column(name = "unit_price")
    val unitPrice: String?,
    @Column(name = "valid_to")
    val validTo: String?,
    val promo: String?,
    @Column(name = "image_url", length = 1024)
    val imageUrl: String?,
    @Column(name = "refreshed_at", nullable = false)
    val refreshedAt: Instant,
    @Id
    @Column(nullable = false)
    @GeneratedValue
    val id: UUID? = null,
)
