package org.kotletai.backend.logging

import ch.qos.logback.classic.LoggerContext
import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Bounded in-memory ring buffer of the most recent log events. Memory stays capped at
 * [capacity] entries; the oldest are dropped first. Reads and writes are synchronized
 * because Logback appends from arbitrary application threads.
 */
@Component
class InMemoryLogStore(
    @Value("\${logs.buffer-size:500}") private val capacity: Int,
) {
    private val lock = Any()
    private val entries = ArrayDeque<LogEntry>()

    fun add(entry: LogEntry) =
        synchronized(lock) {
            if (entries.size >= capacity) {
                entries.removeFirst()
            }
            entries.addLast(entry)
        }

    /** Returns entries oldest-first. When [limit] is set, only the most recent [limit] are returned. */
    fun snapshot(limit: Int?): List<LogEntry> =
        synchronized(lock) {
            val all = entries.toList()
            if (limit == null || limit >= all.size) all else all.takeLast(limit)
        }

    @PostConstruct
    fun register() {
        val context = LoggerFactory.getILoggerFactory() as? LoggerContext ?: return
        val appender = InMemoryLogAppender(this)
        appender.context = context
        appender.start()
        context.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(appender)
    }
}
