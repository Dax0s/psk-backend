package org.kotletai.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "audit_log")
class AuditLog(
    @Column(name = "event_time", nullable = false)
    val eventTime: Instant,
    @Column(nullable = false, length = 255)
    val username: String,
    @Column(name = "roles", length = 512)
    val roles: String,
    @Column(name = "class_name", nullable = false, length = 255)
    val className: String,
    @Column(name = "method_name", nullable = false, length = 255)
    val methodName: String,
    @Column(nullable = false, length = 16)
    val outcome: String,
    @Column(name = "elapsed_ms", nullable = false)
    val elapsedMs: Long,
    @Column(name = "error_message", length = 2048)
    val errorMessage: String? = null,
    @Column(columnDefinition = "TEXT")
    val args: String? = null,
    @Id
    @Column(nullable = false)
    @GeneratedValue
    val id: UUID? = null,
)
