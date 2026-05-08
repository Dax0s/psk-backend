package org.kotletai.backend.exception

import org.springframework.web.client.HttpClientErrorException

class NotFoundException(
    message: String,
) : RuntimeException(message)
