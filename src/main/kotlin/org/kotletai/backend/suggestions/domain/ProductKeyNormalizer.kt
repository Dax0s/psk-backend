package org.kotletai.backend.suggestions.domain

import java.util.Locale

private val whitespaceRegex = Regex("\\s+")

fun normalizeProductKey(value: String): String = value
    .trim()
    .lowercase(Locale.ROOT)
    .replace(whitespaceRegex, " ")
