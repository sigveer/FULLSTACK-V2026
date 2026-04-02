package com.iksystem.common.deviation.dto

import com.iksystem.common.deviation.model.DeviationModule
import com.iksystem.common.deviation.model.DeviationSeverity
import com.iksystem.common.deviation.model.DeviationStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateDeviationRequest(
    @field:NotNull(message = "Module is required")
    val module: DeviationModule,

    @field:NotBlank(message = "Title is required")
    @field:Size(max = 255, message = "Title cannot exceed 255 characters")
    val title: String,

    @field:NotBlank(message = "Description is required")
    @field:Size(max = 5000, message = "Description cannot exceed 5000 characters")
    val description: String,

    @field:Size(max = 5000, message = "Immediate action cannot exceed 5000 characters")
    val immediateAction: String? = null,

    @field:NotNull(message = "Severity is required")
    val severity: DeviationSeverity,

    val assignedToUserId: Long? = null,
)

data class UpdateDeviationRequest(
    val module: DeviationModule? = null,

    @field:Size(max = 255, message = "Title cannot exceed 255 characters")
    val title: String? = null,

    @field:Size(max = 5000, message = "Description cannot exceed 5000 characters")
    val description: String? = null,

    @field:Size(max = 5000, message = "Immediate action cannot exceed 5000 characters")
    val immediateAction: String? = null,

    val severity: DeviationSeverity? = null,
    val assignedToUserId: Long? = null,
)

data class UpdateDeviationStatusRequest(
    @field:NotNull(message = "Status is required")
    val status: DeviationStatus,
)

data class DeviationResponse(
    val id: Long,
    val organizationId: Long,
    val module: DeviationModule,
    val title: String,
    val description: String,
    val immediateAction: String?,
    val severity: DeviationSeverity,
    val status: DeviationStatus,
    val reportedByUserId: Long,
    val reportedByUserName: String,
    val assignedToUserId: Long?,
    val assignedToUserName: String?,
    val reportedAt: String,
    val resolvedAt: String?,
    val createdAt: String,
    val updatedAt: String,
)
