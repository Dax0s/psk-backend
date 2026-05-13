package org.kotletai.backend.scheduler

import org.kotletai.backend.service.ProductSuggestionService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class MaximaRefreshScheduler(
    private val productSuggestionService: ProductSuggestionService,
) {
    @Scheduled(cron = "0 0 4 * * *", zone = "Europe/Vilnius")
    fun refreshDaily() {
        productSuggestionService.refreshFromMaxima()
    }
}
