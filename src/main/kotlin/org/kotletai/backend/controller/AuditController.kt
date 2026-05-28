package org.kotletai.backend.controller

import org.kotletai.backend.aspect.AuditProperties
import org.kotletai.backend.config.CurrentUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/audit")
class AuditController(
    private val properties: AuditProperties,
    private val currentUser: CurrentUser,
) {
    @GetMapping
    fun status(): Map<String, Any> {
        currentUser.requireAdmin()
        return mapOf(
            "enabled" to properties.enabled,
            "includeArgs" to properties.includeArgs,
        )
    }

    @PutMapping
    fun update(
        @RequestParam(required = false) enabled: Boolean?,
        @RequestParam(required = false) includeArgs: Boolean?,
    ): Map<String, Any> {
        currentUser.requireAdmin()
        enabled?.let { properties.enabled = it }
        includeArgs?.let { properties.includeArgs = it }
        return status()
    }
}
