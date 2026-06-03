package org.kotletai.backend.aspect

import org.kotletai.backend.entity.AuditLog
import org.kotletai.backend.repository.AuditLogRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class AuditLogWriter(
    private val repository: AuditLogRepository,
) {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun record(entry: AuditLog) {
        repository.save(entry)
    }
}
