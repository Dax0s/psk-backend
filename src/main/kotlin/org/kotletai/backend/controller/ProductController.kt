package org.kotletai.backend.controller

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.kotletai.backend.entity.ProductCategory
import org.kotletai.backend.model.ProductSuggestionResponse
import org.kotletai.backend.model.toResponse
import org.kotletai.backend.service.ProductSuggestionService
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
@RequestMapping("/api/products")
class ProductController(
    private val productSuggestionService: ProductSuggestionService,
) {
    @GetMapping("/suggestions")
    @ResponseStatus(HttpStatus.OK)
    fun suggest(
        @RequestParam q: String,
        @RequestParam(defaultValue = "10") @Min(1) @Max(25) limit: Int,
    ): List<ProductSuggestionResponse> = productSuggestionService.suggest(q, limit).map { it.toResponse() }

    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    fun categories(): List<ProductCategory> = ProductCategory.entries
}
