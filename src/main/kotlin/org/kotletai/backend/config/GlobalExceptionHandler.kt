package org.kotletai.backend.config

import org.kotletai.backend.exception.AppException
import org.kotletai.backend.exception.BadRequestException
import org.kotletai.backend.exception.ConflictException
import org.kotletai.backend.exception.ForbiddenException
import org.kotletai.backend.exception.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.function.Consumer

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(AppException::class)
    fun handleAppException(e: AppException): ResponseEntity<ProblemDetail> {
        val status =
            when (e) {
                is NotFoundException -> HttpStatus.NOT_FOUND
                is ConflictException -> HttpStatus.CONFLICT
                is ForbiddenException -> HttpStatus.FORBIDDEN
                is BadRequestException -> HttpStatus.BAD_REQUEST
            }
        return ResponseEntity
            .status(status)
            .body(ProblemDetail.forStatusAndDetail(status, e.message))
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): MutableMap<String?, String?> {
        val errors: MutableMap<String?, String?> = HashMap()
        ex.bindingResult.allErrors.forEach(
            Consumer { error: ObjectError? ->
                val fieldName = (error as FieldError).field
                val errorMessage = error.defaultMessage
                errors[fieldName] = errorMessage
            },
        )
        return errors
    }
}
