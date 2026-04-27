package org.kotletai.backend.controller

import org.kotletai.backend.dto.CreateFamilyRequest
import org.kotletai.backend.dto.FamilyDetailResponse
import org.kotletai.backend.dto.FamilyResponse
import org.kotletai.backend.dto.FamilySummaryResponse
import org.kotletai.backend.dto.JoinFamilyRequest
import org.kotletai.backend.service.FamilyService
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
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
@RequestMapping("/api/families")
class FamilyController(
    private val familyService: FamilyService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createFamily(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody request: CreateFamilyRequest,
    ): FamilyResponse = familyService.createFamily(jwt.subject, request.name, request.email)

    @PostMapping("/join")
    fun joinFamily(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody request: JoinFamilyRequest,
    ): FamilyResponse = familyService.joinFamily(jwt.subject, request.inviteCode, request.email)

    @GetMapping
    fun getMyFamilies(
        @AuthenticationPrincipal jwt: Jwt,
    ): List<FamilySummaryResponse> = familyService.getMyFamilies(jwt.subject)

    @GetMapping("/{familyId}")
    fun getFamilyDetails(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable familyId: UUID,
    ): FamilyDetailResponse = familyService.getFamilyDetails(jwt.subject, familyId)

    @DeleteMapping("/{familyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteFamily(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable familyId: UUID,
    ) = familyService.deleteFamily(jwt.subject, familyId)

    @DeleteMapping("/{familyId}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeMember(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable familyId: UUID,
        @PathVariable userId: String,
    ) = familyService.removeMember(jwt.subject, familyId, userId)

    @PostMapping("/{familyId}/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun leaveFamily(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable familyId: UUID,
    ) = familyService.leaveFamily(jwt.subject, familyId)
}
