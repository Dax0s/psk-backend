package org.kotletai.backend.repository

import org.kotletai.backend.entity.PinnedProduct
import org.kotletai.backend.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PinnedProductRepository : JpaRepository<PinnedProduct, UUID> {
    fun findByUserOrderBySortOrderAsc(user: User): List<PinnedProduct>

    fun findByIdAndUser(
        id: UUID,
        user: User,
    ): PinnedProduct?
}
