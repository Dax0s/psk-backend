package org.kotletai.backend.suggestions.domain

data class SuggestionScope(
    val type: SuggestionScopeType,
    val referenceId: String,
) {
    init {
        require(referenceId.isNotBlank()) { "Suggestion scope reference id must not be blank." }
    }
}
