package org.kotletai.backend.repository

import org.kotletai.backend.entity.ProductOffer
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface ProductOfferRepository : JpaRepository<ProductOffer, UUID> {
    @Query(
        """
        SELECT p FROM ProductOffer p
        WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))
        ORDER BY
            CASE WHEN p.discountPct IS NULL THEN 1 ELSE 0 END,
            p.discountPct DESC,
            p.name ASC
        """,
    )
    fun search(
        q: String,
        pageable: Pageable,
    ): List<ProductOffer>

    @Modifying
    @Query("DELETE FROM ProductOffer p WHERE p.source = :source")
    fun deleteBySource(source: String)
}
