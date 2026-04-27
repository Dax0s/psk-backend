package org.kotletai.backend.exception

import org.springframework.http.HttpStatus

sealed class AppException(
    val status: HttpStatus,
    val errorCode: String,
    override val message: String,
) : RuntimeException(message)

class NotFoundException(errorCode: String, message: String) :
    AppException(HttpStatus.NOT_FOUND, errorCode, message)

class ConflictException(errorCode: String, message: String) :
    AppException(HttpStatus.CONFLICT, errorCode, message)

class ForbiddenException(errorCode: String, message: String) :
    AppException(HttpStatus.FORBIDDEN, errorCode, message)

class BadRequestException(errorCode: String, message: String) :
    AppException(HttpStatus.BAD_REQUEST, errorCode, message)
