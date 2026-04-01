package com.iksystem.`ik-common`.training.dto

import com.iksystem.`ik-common`.training.model.TrainingStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateTrainingLogRequest(
    @field:NotNull(message = "Employee user ID is required")
    val employeeUserId: Long,

    @field:NotBlank(message = "Title is required")
    @field:Size(max = 255, message = "Title cannot exceed 255 characters")
    val title: String,

    @field:Size(max = 5000, message = "Description cannot exceed 5000 characters")
    val description: String? = null,

    val completedAt: String? = null,

    val expiresAt: String? = null,

    @field:NotNull(message = "Status is required")
    val status: TrainingStatus,
)

data class UpdateTrainingLogRequest(
    val employeeUserId: Long? = null,

    @field:Size(max = 255, message = "Title cannot exceed 255 characters")
    val title: String? = null,

    @field:Size(max = 5000, message = "Description cannot exceed 5000 characters")
    val description: String? = null,

    val completedAt: String? = null,

    val expiresAt: String? = null,

    val status: TrainingStatus? = null,
)

data class TrainingLogResponse(
    val id: Long,
    val organizationId: Long,
    val employeeUserId: Long,
    val employeeUserName: String,
    val loggedByUserId: Long,
    val loggedByUserName: String,
    val title: String,
    val description: String?,
    val completedAt: String?,
    val expiresAt: String?,
    val status: TrainingStatus,
    val createdAt: String,
    val updatedAt: String,
)
