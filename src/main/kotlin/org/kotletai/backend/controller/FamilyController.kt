package org.kotletai.backend.controller

import jakarta.validation.Valid
import org.kotletai.backend.config.CurrentUser
import org.kotletai.backend.dto.CreateFamilyRequest
import org.kotletai.backend.dto.FamilyResponse
import org.kotletai.backend.dto.JoinFamilyRequest
import org.kotletai.backend.dto.toDetailResponse
import org.kotletai.backend.dto.toResponse
import org.kotletai.backend.service.FamilyService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/family")
class FamilyController(
    private val familyService: FamilyService,
    private val currentUser: CurrentUser,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createFamily(@Valid @RequestBody request: CreateFamilyRequest): FamilyResponse =
        familyService.createFamily(request.name).toResponse(currentUser.user)

    @PostMapping("/join")
    fun joinFamily(@Valid @RequestBody request: JoinFamilyRequest): FamilyResponse =
        familyService.joinFamily(request.inviteCode).family.toResponse(currentUser.user)

    @GetMapping
    fun getFamilies(): List<FamilyResponse> =
        familyService.getFamilies().map { it.family.toResponse(currentUser.user) }

    @GetMapping("/{familyId}")
    fun getFamily(@PathVariable familyId: UUID): FamilyResponse =
        familyService.getFamily(familyId).toDetailResponse(currentUser.user)

    @DeleteMapping("/{familyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteFamily(@PathVariable familyId: UUID) = familyService.deleteFamily(familyId)

    @DeleteMapping("/{familyId}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeMember(
        @PathVariable familyId: UUID,
        @PathVariable userId: String,
    ) = familyService.removeMember(familyId, userId)

    @PostMapping("/{familyId}/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun leaveFamily(@PathVariable familyId: UUID) = familyService.leaveFamily(familyId)
}
