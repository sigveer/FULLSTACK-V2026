package com.iksystem.common.organization.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * Request payload for creating a new organization.
 *
 * @property name Display name of the organization (required, max 200 chars).
 * @property orgNumber Optional external organization/business registry number.
 */
@Schema(description = "Create organization request payload")
data class CreateOrganizationRequest(
    @Schema(description = "Organization name (unique)", example = "IK System")
    @field:NotBlank @field:Size(max = 200)
    val name: String,
    @Schema(description = "External organization/business number", example = "123456789", nullable = true)
    val orgNumber: String? = null,
)

/**
 * Response DTO returned when reading organization data.
 *
 * @property id The organization's primary key.
 * @property name The organization's display name.
 * @property orgNumber The external organization number, or `null` if not set.
 */
@Schema(description = "Organization response")
data class OrganizationResponse(
    @Schema(description = "Organization ID", example = "1")
    val id: Long,
    @Schema(description = "Organization name", example = "IK System")
    val name: String,
    @Schema(description = "External organization number", example = "123456789", nullable = true)
    val orgNumber: String?,
)
