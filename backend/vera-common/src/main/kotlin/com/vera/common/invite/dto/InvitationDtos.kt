package com.iksystem.common.invite.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "Request to invite a user to the organization")
data class InviteUserRequest(
    @Schema(description = "Email address of the person to invite", example = "ola@example.com")
    @field:NotBlank @field:Email
    val email: String,

    @Schema(description = "Role to assign", example = "EMPLOYEE")
    @field:NotBlank
    val role: String,
)

@Schema(description = "Request to accept an invitation")
data class AcceptInviteRequest(
    @Schema(description = "Password for new account (required if user doesn't exist)")
    @field:Size(min = 8, max = 128)
    val password: String? = null,

    @Schema(description = "Full name (required if user doesn't exist)")
    @field:Size(max = 100)
    val fullName: String? = null,

    @Schema(description = "Phone number (required if user doesn't exist)")
    @field:Size(max = 30)
    val phoneNumber: String? = null,
)

@Schema(description = "Public details about a pending invitation")
data class InviteDetailsResponse(
    @Schema(description = "Invited email address")
    val email: String,
    @Schema(description = "Organization name")
    val organizationName: String,
    @Schema(description = "Whether a user account already exists for this email")
    val existingUser: Boolean,
)
