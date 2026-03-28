package com.ik.ikcommon.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

/**
 * Global exception handler that translates domain exceptions into
 * consistent JSON error responses.
 *
 * Every handler returns an [ErrorResponse] envelope so the frontend
 * can rely on a single error shape across all endpoints.
 */
@ControllerAdvice
class GlobalExceptionHandler {

    /** Top-level error envelope returned by every error response. */
    data class ErrorResponse(
        val error: ErrorDetail,
    )

    /**
     * Describes a single error.
     *
     * @property code Machine-readable error code (e.g. `"not_found"`).
     * @property message Human-readable description of the error.
     * @property details Optional list of per-field validation errors.
     */
    data class ErrorDetail(
        val code: String,
        val message: String,
        val details: List<FieldError>? = null,
    )

    /**
     * Describes a validation error on a specific request field.
     *
     * @property field The name of the invalid field.
     * @property message The validation failure message.
     */
    data class FieldError(
        val field: String,
        val message: String,
    )

    /** Handles Bean Validation failures (`@Valid`). Returns `400` with per-field details. */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val details = ex.bindingResult.fieldErrors.map {
            FieldError(it.field, it.defaultMessage ?: "Invalid value")
        }
        return ResponseEntity.badRequest().body(
            ErrorResponse(ErrorDetail("validation_error", "Validation failed", details))
        )
    }

    /** Handles [NotFoundException]. Returns `404`. */
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse(ErrorDetail("not_found", ex.message ?: "Resource not found"))
        )

    /** Handles [ConflictException]. Returns `409`. */
    @ExceptionHandler(ConflictException::class)
    fun handleConflict(ex: ConflictException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT).body(
            ErrorResponse(ErrorDetail("conflict", ex.message ?: "Conflict"))
        )

    /** Handles [UnauthorizedException]. Returns `401`. */
    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorized(ex: UnauthorizedException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ErrorResponse(ErrorDetail("unauthorized", ex.message ?: "Unauthorized"))
        )

    /** Handles [ForbiddenException]. Returns `403`. */
    @ExceptionHandler(ForbiddenException::class)
    fun handleForbidden(ex: ForbiddenException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ErrorResponse(ErrorDetail("forbidden", ex.message ?: "Forbidden"))
        )

    /** Handles [BadRequestException]. Returns `400`. */
    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(ex: BadRequestException): ResponseEntity<ErrorResponse> =
        ResponseEntity.badRequest().body(
            ErrorResponse(ErrorDetail("bad_request", ex.message ?: "Bad request"))
        )

    /** Handles Spring Security [AccessDeniedException] (e.g. `@PreAuthorize` failures). Returns `403`. */
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ErrorResponse(ErrorDetail("forbidden", "Access denied"))
        )

    /** Catch-all for any unhandled exception. Returns `500 Internal Server Error`. */
    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorResponse(ErrorDetail("internal_error", "Internal server error"))
        )
}
