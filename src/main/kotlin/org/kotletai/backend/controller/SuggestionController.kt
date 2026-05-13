package org.kotletai.backend.controller

import org.kotletai.backend.model.SuggestedProductResponse
import org.kotletai.backend.service.SuggestionService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/suggestion")
class SuggestionController(
    private val suggestionService: SuggestionService,
) {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getSuggestions(): List<SuggestedProductResponse> = suggestionService.getSuggestions()
}
