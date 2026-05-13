package org.kotletai.backend.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.kotletai.backend.entity.ProductOffer
import org.kotletai.backend.repository.ProductOfferRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant

private val log = KotlinLogging.logger {}

@Service
class ProductSuggestionService(
    private val productOfferRepository: ProductOfferRepository,
    private val maximaScraperService: MaximaScraperService,
) {
    @Transactional(readOnly = true)
    fun suggest(
        query: String,
        limit: Int = 10,
    ): List<ProductOffer> {
        val q = query.trim()
        if (q.length < 2) return emptyList()
        return productOfferRepository.search(q, PageRequest.of(0, limit))
    }

    @Transactional
    fun refreshFromMaxima() {
        log.info { "Refreshing Maxima offers..." }
        val scraped =
            try {
                maximaScraperService.fetchAll()
            } catch (e: Exception) {
                log.error(e) { "Maxima scrape failed; keeping existing offers" }
                return
            }

        val now = Instant.now()
        val rows =
            scraped.flatMap { (category, offers) ->
                offers.map { o ->
                    ProductOffer(
                        source = SOURCE_MAXIMA,
                        category = category,
                        name = o.name,
                        price = o.price?.let { BigDecimal.valueOf(it).setScale(2, RoundingMode.HALF_UP) },
                        oldPrice = o.oldPrice,
                        discountPct = o.discountPct,
                        unitPrice = o.unitPrice,
                        validTo = o.validTo,
                        promo = o.promo,
                        imageUrl = o.imageUrl,
                        refreshedAt = now,
                    )
                }
            }

        productOfferRepository.deleteBySource(SOURCE_MAXIMA)
        productOfferRepository.saveAll(rows)
        log.info { "Maxima offers refreshed: ${rows.size} rows across ${scraped.size} categories" }
    }

    companion object {
        const val SOURCE_MAXIMA = "MAXIMA"
    }
}
