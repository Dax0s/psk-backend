package org.kotletai.backend.controller

import org.kotletai.backend.config.CurrentUser
import org.kotletai.backend.model.CurrentUserResponse
import org.kotletai.backend.model.toCurrentUserResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val currentUser: CurrentUser,
) {
    @GetMapping("/me")
    fun me(): CurrentUserResponse = currentUser.user.toCurrentUserResponse()
}
