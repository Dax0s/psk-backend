package org.kotletai.backend.config

import org.kotletai.backend.entity.User
import org.kotletai.backend.entity.UserRole
import org.kotletai.backend.exception.ForbiddenException
import org.kotletai.backend.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope
import java.util.UUID

@Component
@RequestScope
class CurrentUser(
    val userRepository: UserRepository,
) {
    val cognitoId: UUID by lazy {
        val jwt =
            SecurityContextHolder
                .getContext()
                .authentication
                ?.principal as Jwt
        UUID.fromString(jwt.subject)
    }

    val user: User by lazy {
        userRepository.findByCognitoId(cognitoId) ?: userRepository.save(User(cognitoId))
    }

    val isAdmin: Boolean
        get() = user.role == UserRole.ADMIN

    fun requireAdmin() {
        if (!isAdmin) {
            throw ForbiddenException("Admin role required")
        }
    }
}
