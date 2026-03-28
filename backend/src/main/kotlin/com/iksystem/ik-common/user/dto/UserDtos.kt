package com.iksystem.`ik-common`.user.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * Response DTO for user identity (no role/org — those come from memberships).
 */
@Schema(description = "User identity response")
data class UserResponse(
    @Schema(description = "User ID", example = "1")
    val id: Long,
    @Schema(description = "Email address", example = "admin@iksystem.local")
    val email: String,
    @Schema(description = "Full display name", example = "Admin User")
    val fullName: String,
    @Schema(description = "Phone number", example = "+4712345678")
    val phoneNumber: String,
    @Schema(description = "Whether the account is active", example = "true")
    val active: Boolean,
)

/**
 * Request payload for creating a new user and adding them to the caller's organization.
 */
@Schema(description = "Create user request payload")
data class CreateUserRequest(
    @Schema(description = "Unique email address", example = "newuser@example.com")
    @field:NotBlank @field:Email
    val email: String,
    @Schema(description = "Password (8-128 characters)", example = "securepass123")
    @field:NotBlank @field:Size(min = 8, max = 128)
    val password: String,
    @Schema(description = "Full display name", example = "Kari Nordmann")
    @field:NotBlank @field:Size(max = 100)
    val fullName: String,
    @Schema(description = "Phone number", example = "+4798765432")
    @field:NotBlank @field:Size(max = 30)
    val phoneNumber: String,
    @Schema(description = "Role to assign in the caller's organization", example = "EMPLOYEE", allowableValues = ["ADMIN", "MANAGER", "EMPLOYEE"])
    @field:NotBlank
    val role: String,
)

/**
 * Request payload for changing a membership's role.
 */
@Schema(description = "Update user role request payload")
data class UpdateUserRoleRequest(
    @Schema(description = "New role", example = "MANAGER", allowableValues = ["ADMIN", "MANAGER", "EMPLOYEE"])
    @field:NotBlank
    val role: String,
)
