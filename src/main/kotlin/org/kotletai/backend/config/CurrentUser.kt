package org.kotletai.backend.config

import org.kotletai.backend.entity.User
import org.kotletai.backend.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope

@Component
@RequestScope
class CurrentUser(
    val userRepository: UserRepository,
) {
    val cognitoId: String by lazy {
        val jwt =
            SecurityContextHolder
                .getContext()
                .authentication
                ?.principal as Jwt
        jwt.subject
    }

    val user: User by lazy {
        userRepository.findByCognitoId(cognitoId) ?: userRepository.save(User(cognitoId))
    }
}
