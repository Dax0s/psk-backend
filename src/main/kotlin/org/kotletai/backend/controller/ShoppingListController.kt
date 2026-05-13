package org.kotletai.backend.controller

import jakarta.validation.Valid
import org.kotletai.backend.model.CreateShoppingListItemRequest
import org.kotletai.backend.model.ShoppingListItemResponse
import org.kotletai.backend.model.ShoppingListRequest
import org.kotletai.backend.model.ShoppingListResponse
import org.kotletai.backend.model.UpdateShoppingListItemRequest
import org.kotletai.backend.model.toResponse
import org.kotletai.backend.service.ShoppingListService
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
@RequestMapping("/api/shopping-list")
class ShoppingListController(
    private val shoppingListService: ShoppingListService,
) {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getShoppingLists(): List<ShoppingListResponse> = shoppingListService.getShoppingLists().map { it.toResponse() }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getShoppingList(
        @PathVariable id: UUID,
    ): ShoppingListResponse = shoppingListService.getShoppingList(id).toResponse()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createShoppingList(
        @Valid @RequestBody request: ShoppingListRequest,
    ): ShoppingListResponse = shoppingListService.createShoppingList(request.name, request.familyId).toResponse()

    @GetMapping("/family/{familyId}")
    @ResponseStatus(HttpStatus.OK)
    fun getShoppingListsByFamily(
        @PathVariable familyId: UUID,
    ): List<ShoppingListResponse> = shoppingListService.getShoppingListsByFamily(familyId).map { it.toResponse() }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun updateShoppingList(
        @PathVariable id: UUID,
        @Valid @RequestBody request: ShoppingListRequest,
    ): ShoppingListResponse = shoppingListService.updateShoppingList(id, request.name).toResponse()

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteShoppingList(
        @PathVariable id: UUID,
    ) = shoppingListService.deleteShoppingList(id)

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    fun createShoppingListItem(
        @PathVariable id: UUID,
        @Valid @RequestBody request: CreateShoppingListItemRequest,
    ): ShoppingListItemResponse =
        shoppingListService
            .createShoppingListItem(id, request.name, request.quantity, request.category)
            .toResponse()

    @PutMapping("/{id}/item/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    fun updateShoppingListItem(
        @PathVariable id: UUID,
        @PathVariable itemId: UUID,
        @Valid @RequestBody request: UpdateShoppingListItemRequest,
    ): ShoppingListItemResponse =
        shoppingListService
            .updateShoppingListItem(id, itemId, request.name, request.quantity, request.checked, request.category)
            .toResponse()

    @DeleteMapping("/{id}/item/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteShoppingListItem(
        @PathVariable id: UUID,
        @PathVariable itemId: UUID,
    ) = shoppingListService.deleteShoppingListItem(id, itemId)
}
