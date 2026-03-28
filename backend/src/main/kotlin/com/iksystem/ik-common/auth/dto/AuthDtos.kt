package com.iksystem.`ik-common`.auth.dto

import com.iksystem.`ik-common`.membership.dto.MembershipSummary
import com.iksystem.`ik-common`.user.dto.UserResponse
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * Request payload for user login.
 */
@Schema(description = "Login request payload")
data class LoginRequest(
    @Schema(description = "User email address", example = "admin@iksystem.local")
    @field:NotBlank @field:Email
    val email: String,
    @Schema(description = "User password", example = "password")
    @field:NotBlank
    val password: String,
)

/**
 * Response from login — contains a pre-auth token and list of memberships.
 * The client must then call `/auth/select-org` with one of the membership org IDs.
 */
@Schema(description = "Login response with pre-auth token and available organizations")
data class LoginResponse(
    @Schema(description = "Pre-auth JWT token (use with select-org endpoint)")
    val preAuthToken: String,
    @Schema(description = "User profile")
    val user: UserResponse,
    @Schema(description = "Organizations the user belongs to")
    val memberships: List<MembershipSummary>,
)

/**
 * Request payload for selecting an organization after login.
 */
@Schema(description = "Organization selection request")
data class SelectOrgRequest(
    @Schema(description = "ID of the organization to select", example = "1")
    val organizationId: Long,
)

/**
 * Request payload for new user registration (no org — user is pure identity).
 */
@Schema(description = "Registration request payload")
data class RegisterRequest(
    @Schema(description = "Unique email address", example = "newuser@example.com")
    @field:NotBlank @field:Email
    val email: String,
    @Schema(description = "Password (8-128 characters)", example = "securepass123")
    @field:NotBlank @field:Size(min = 8, max = 128)
    val password: String,
    @Schema(description = "Full display name", example = "Ola Nordmann")
    @field:NotBlank @field:Size(max = 100)
    val fullName: String,
    @Schema(description = "Phone number", example = "+4712345678")
    @field:NotBlank @field:Size(max = 30)
    val phoneNumber: String,
)

/**
 * Request payload for refreshing an expired access token.
 */
@Schema(description = "Token refresh request payload")
data class RefreshRequest(
    @Schema(description = "Opaque refresh token from select-org response")
    @field:NotBlank
    val refreshToken: String,
)

/**
 * Response returned after successful org selection or token refresh.
 * Contains a full JWT scoped to the selected organization.
 */
@Schema(description = "Authentication response with org-scoped tokens and user profile")
data class AuthResponse(
    @Schema(description = "JWT access token for Authorization header")
    val accessToken: String,
    @Schema(description = "Opaque refresh token for obtaining new access tokens")
    val refreshToken: String,
    @Schema(description = "Access token lifetime in seconds", example = "900")
    val expiresIn: Long,
    @Schema(description = "Authenticated user profile")
    val user: UserResponse,
    @Schema(description = "Selected organization ID", example = "1")
    val organizationId: Long,
    @Schema(description = "User's role in the selected organization", example = "ADMIN")
    val role: String,
)
