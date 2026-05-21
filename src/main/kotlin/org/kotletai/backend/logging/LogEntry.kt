package org.kotletai.backend.logging

import java.time.Instant

data class LogEntry(
    val timestamp: Instant,
    val level: String,
    val logger: String,
    val thread: String,
    val message: String,
)
