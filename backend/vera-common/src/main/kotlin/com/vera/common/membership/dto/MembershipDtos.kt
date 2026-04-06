package com.iksystem.common.membership.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * Summary of a user's membership in an organization, returned during login.
 */
@Schema(description = "Summary of a membership for organization selection")
data class MembershipSummary(
    @Schema(description = "Membership ID", example = "1")
    val membershipId: Long,
    @Schema(description = "Organization ID", example = "1")
    val organizationId: Long,
    @Schema(description = "Organization name", example = "IK System")
    val organizationName: String,
    @Schema(description = "Role in this organization", example = "ADMIN")
    val role: String,
)

/**
 * Full membership response including user details.
 */
@Schema(description = "Membership response with role and organization info")
data class MembershipResponse(
    @Schema(description = "Membership ID", example = "1")
    val id: Long,
    @Schema(description = "User ID", example = "1")
    val userId: Long,
    @Schema(description = "User email", example = "admin@iksystem.local")
    val userEmail: String,
    @Schema(description = "User full name", example = "Admin User")
    val userFullName: String,
    @Schema(description = "Organization ID", example = "1")
    val organizationId: Long,
    @Schema(description = "Role in this organization", example = "ADMIN", allowableValues = ["ADMIN", "MANAGER", "EMPLOYEE"])
    val role: String,
)
