package org.kotletai.backend.aspect

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class AuditProperties(
    @Value("\${audit.enabled:true}") initialEnabled: Boolean,
    @Value("\${audit.include-args:false}") initialIncludeArgs: Boolean,
) {
    @Volatile
    var enabled: Boolean = initialEnabled

    @Volatile
    var includeArgs: Boolean = initialIncludeArgs
}
