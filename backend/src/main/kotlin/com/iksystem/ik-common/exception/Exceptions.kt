package com.ik.ikcommon.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/** Thrown when a requested resource does not exist. Maps to `404 Not Found`. */
@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException(message: String) : RuntimeException(message)

/** Thrown when a create/update would violate a uniqueness constraint. Maps to `409 Conflict`. */
@ResponseStatus(HttpStatus.CONFLICT)
class ConflictException(message: String) : RuntimeException(message)

/** Thrown when authentication fails or is missing. Maps to `401 Unauthorized`. */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
class UnauthorizedException(message: String) : RuntimeException(message)

/** Thrown when the caller lacks permission for the requested action. Maps to `403 Forbidden`. */
@ResponseStatus(HttpStatus.FORBIDDEN)
class ForbiddenException(message: String) : RuntimeException(message)

/** Thrown when the request payload is invalid. Maps to `400 Bad Request`. */
@ResponseStatus(HttpStatus.BAD_REQUEST)
class BadRequestException(message: String) : RuntimeException(message)
