package org.kotletai.backend.service

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.springframework.stereotype.Service

data class ScrapedOffer(
    val name: String,
    val price: Double?,
    val oldPrice: String?,
    val discountPct: Int?,
    val unitPrice: String?,
    val validTo: String?,
    val promo: String?,
    val imageUrl: String?,
)

@Service
class MaximaScraperService {
    fun fetchAll(): Map<String, List<ScrapedOffer>> {
        val doc =
            Jsoup
                .connect(URL)
                .userAgent("Mozilla/5.0")
                .timeout(15_000)
                .get()

        val out = linkedMapOf<String, MutableList<ScrapedOffer>>()
        var currentCategory: String? = null
        val seen = mutableSetOf<String>()

        for (el in doc.allElements) {
            when (el.tagName()) {
                "h2" ->
                    el
                        .text()
                        .trim()
                        .takeIf { it.isNotBlank() }
                        ?.let { currentCategory = it }

                "h4" -> {
                    val cat = currentCategory ?: continue
                    val card = offerCardAncestor(el)
                    val offer = parseOffer(card) ?: continue
                    val key = "$cat|${offer.name}"
                    if (seen.add(key)) {
                        out.getOrPut(cat) { mutableListOf() }.add(offer)
                    }
                }
            }
        }
        return out.filterValues { it.isNotEmpty() }
    }

    private fun offerCardAncestor(h4: Element): Element {
        var cur = h4
        while (true) {
            val parent = cur.parent() ?: return cur
            if (parent.select("h4").size > 1) return cur
            if (parent.tagName() in ROOT_TAGS) return cur
            cur = parent
        }
    }

    private fun parseOffer(el: Element): ScrapedOffer? {
        val name = el.selectFirst("h4")?.text()?.trim() ?: return null

        val intPart = el.selectFirst(".offer-price-tag__price-integer")?.text()
        val fracPart = el.selectFirst(".offer-price-tag__price-fraction")?.text()
        val price =
            if (intPart != null && fracPart != null) {
                "$intPart.$fracPart".toDoubleOrNull()
            } else {
                null
            }

        val texts = el.select("*").map { it.ownText().trim() }.filter { it.isNotBlank() }

        val oldPrice = texts.firstOrNull { OLD_PRICE_REGEX.matches(it) }
        val discountPct =
            texts
                .firstOrNull { DISCOUNT_PCT_REGEX.matches(it) }
                ?.filter(Char::isDigit)
                ?.toIntOrNull()
        val unitPrice = texts.firstOrNull { UNIT_PRICE_REGEX.matches(it) }
        val validTo =
            texts
                .firstOrNull { VALID_TO_REGEX.matches(it) }
                ?.removePrefix("Iki ")
        val promo = texts.firstOrNull { it in PROMO_TAGS }
        val imageUrl =
            el
                .selectFirst("img")
                ?.absUrl("src")
                ?.takeUnless { it.contains("placeholder") }

        return ScrapedOffer(name, price, oldPrice, discountPct, unitPrice, validTo, promo, imageUrl)
    }

    companion object {
        private const val URL = "https://www.maxima.lt/pasiulymai"
        private val PROMO_TAGS = setOf("1+1", "2 vnt. už", "Perkant 2 ar daugiau")
        private val ROOT_TAGS = setOf("body", "html")
        private val OLD_PRICE_REGEX = Regex("""\d+,\d+\s*€""")
        private val DISCOUNT_PCT_REGEX = Regex("""-\d+\s*%""")
        private val UNIT_PRICE_REGEX = Regex("""1\s+(kg|l|vnt\.).*€.*""")
        private val VALID_TO_REGEX = Regex("""Iki \d\d\.\d\d""")
    }
}
