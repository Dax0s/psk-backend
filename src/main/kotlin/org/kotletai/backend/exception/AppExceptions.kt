package org.kotletai.backend.exception

sealed class AppException(override val message: String) : RuntimeException(message)

class NotFoundException(message: String) : AppException(message)
class ConflictException(message: String) : AppException(message)
class ForbiddenException(message: String) : AppException(message)
class BadRequestException(message: String) : AppException(message)
