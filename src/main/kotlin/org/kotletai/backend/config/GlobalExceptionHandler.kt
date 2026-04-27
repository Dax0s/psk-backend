package org.kotletai.backend.config

import org.kotletai.backend.dto.ErrorResponse
import org.kotletai.backend.exception.AppException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(AppException::class)
    fun handleAppException(ex: AppException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(ex.status)
            .body(ErrorResponse(error = ex.errorCode, message = ex.message))
}
