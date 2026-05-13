package org.kotletai.backend.controller

import jakarta.validation.Valid
import org.kotletai.backend.model.CreatePinnedProductRequest
import org.kotletai.backend.model.PinnedProductResponse
import org.kotletai.backend.model.UpdatePinnedProductRequest
import org.kotletai.backend.model.toResponse
import org.kotletai.backend.service.SuggestionService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/pinned-product")
class PinnedProductController(
    private val suggestionService: SuggestionService,
) {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getPinnedProducts(): List<PinnedProductResponse> = suggestionService.getPinnedProducts().map { it.toResponse() }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createPinnedProduct(
        @Valid @RequestBody request: CreatePinnedProductRequest,
    ): PinnedProductResponse =
        suggestionService
            .createPinnedProduct(
                request.name,
                request.defaultQuantity,
                request.sortOrder,
                request.category,
            ).toResponse()

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun updatePinnedProduct(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdatePinnedProductRequest,
    ): PinnedProductResponse =
        suggestionService
            .updatePinnedProduct(
                id,
                request.name,
                request.defaultQuantity,
                request.sortOrder,
                request.category,
            ).toResponse()

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePinnedProduct(
        @PathVariable id: UUID,
    ) = suggestionService.deletePinnedProduct(id)
}
