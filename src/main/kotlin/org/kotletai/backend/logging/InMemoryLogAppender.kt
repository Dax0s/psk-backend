package org.kotletai.backend.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import java.time.Instant

/**
 * Logback appender that mirrors every log event into [InMemoryLogStore] so the admin
 * panel can read recent backend logs over HTTP. Attached to the root logger at startup
 * by [InMemoryLogStore]; it never logs anything itself, so there is no feedback loop.
 */
class InMemoryLogAppender(
    private val store: InMemoryLogStore,
) : AppenderBase<ILoggingEvent>() {
    override fun append(event: ILoggingEvent) {
        store.add(
            LogEntry(
                timestamp = Instant.ofEpochMilli(event.timeStamp),
                level = event.level.toString(),
                logger = event.loggerName,
                thread = event.threadName,
                message = event.formattedMessage,
            ),
        )
    }
}
