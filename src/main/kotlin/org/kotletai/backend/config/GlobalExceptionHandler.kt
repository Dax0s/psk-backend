package org.kotletai.backend.config

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
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(e: NotFoundException): ResponseEntity<ProblemDetail> =
        ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message))

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(e: BadRequestException): ResponseEntity<ProblemDetail> =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message))

    @ExceptionHandler(ConflictException::class)
    fun handleConflictException(e: ConflictException): ResponseEntity<ProblemDetail> =
        ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.message))

    @ExceptionHandler(ForbiddenException::class)
    fun handleForbiddenException(e: ForbiddenException): ResponseEntity<ProblemDetail> =
        ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.message))

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
