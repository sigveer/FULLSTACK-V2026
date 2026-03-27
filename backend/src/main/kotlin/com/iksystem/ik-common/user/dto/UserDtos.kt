package com.iksystem.`ik-common`.user.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * Response DTO returned when reading user data.
 *
 * @property id The user's primary key.
 * @property email The user's email address.
 * @property fullName The user's display name.
 * @property phoneNumber The user's contact phone number.
 * @property role The user's role as a string (e.g. "ADMIN", "MANAGER", "EMPLOYEE").
 * @property organizationId The ID of the organization the user belongs to.
 * @property active Whether the user account is currently enabled.
 */
data class UserResponse(
    val id: Long,
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val role: String,
    val organizationId: Long,
    val active: Boolean,
)

/**
 * Request payload for creating a new user within the caller's organization.
 *
 * @property email Must be a valid, unique email address.
 * @property password Plain-text password (8–128 chars); will be BCrypt-hashed before storage.
 * @property fullName Display name (max 100 chars).
 * @property phoneNumber Contact number (max 30 chars).
 * @property role Role to assign — must be one of ADMIN, MANAGER, or EMPLOYEE.
 */
data class CreateUserRequest(
    @field:NotBlank @field:Email
    val email: String,
    @field:NotBlank @field:Size(min = 8, max = 128)
    val password: String,
    @field:NotBlank @field:Size(max = 100)
    val fullName: String,
    @field:NotBlank @field:Size(max = 30)
    val phoneNumber: String,
    @field:NotBlank
    val role: String,
)

/**
 * Request payload for changing an existing user's role.
 *
 * @property role The new role — must be one of ADMIN, MANAGER, or EMPLOYEE.
 */
data class UpdateUserRoleRequest(
    @field:NotBlank
    val role: String,
)