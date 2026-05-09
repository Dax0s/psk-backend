package org.kotletai.backend.scheduler

import org.kotletai.backend.service.ProductSuggestionService
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class MaximaStartupRefresh(
    private val productSuggestionService: ProductSuggestionService,
) {
    @Async
    @EventListener(ApplicationReadyEvent::class)
    fun onStartup() {
        productSuggestionService.refreshFromMaxima()
    }
}
