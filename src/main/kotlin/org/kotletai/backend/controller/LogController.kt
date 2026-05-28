package org.kotletai.backend.controller

import org.kotletai.backend.config.CurrentUser
import org.kotletai.backend.logging.InMemoryLogStore
import org.kotletai.backend.logging.LogEntry
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/logs")
class LogController(
    private val currentUser: CurrentUser,
    private val store: InMemoryLogStore,
) {
    @GetMapping
    fun logs(
        @RequestParam(required = false) limit: Int?,
    ): List<LogEntry> {
        currentUser.requireAdmin()
        return store.snapshot(limit)
    }
}
