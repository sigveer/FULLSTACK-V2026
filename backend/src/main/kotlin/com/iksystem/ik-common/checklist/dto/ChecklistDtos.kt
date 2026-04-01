package com.iksystem.`ik-common`.checklist.dto

import com.iksystem.`ik-common`.checklist.model.ChecklistFrequency
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateChecklistRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(max = 255, message = "Name cannot exceed 255 characters")
    val name: String,

    @field:Size(max = 5000, message = "Description cannot exceed 5000 characters")
    val description: String? = null,

    @field:NotNull(message = "Frequency is required")
    val frequency: ChecklistFrequency,
)

data class UpdateChecklistRequest(
    @field:Size(max = 255, message = "Name cannot exceed 255 characters")
    val name: String? = null,

    @field:Size(max = 5000, message = "Description cannot exceed 5000 characters")
    val description: String? = null,

    val frequency: ChecklistFrequency? = null,
    val active: Boolean? = null,
)

data class CreateChecklistItemRequest(
    @field:NotBlank(message = "Title is required")
    @field:Size(max = 255, message = "Title cannot exceed 255 characters")
    val title: String,

    @field:Size(max = 5000, message = "Description cannot exceed 5000 characters")
    val description: String? = null,

    val completed: Boolean = false,
)

data class UpdateChecklistItemRequest(
    @field:Size(max = 255, message = "Title cannot exceed 255 characters")
    val title: String? = null,

    @field:Size(max = 5000, message = "Description cannot exceed 5000 characters")
    val description: String? = null,

    val completed: Boolean? = null,
)

data class SetChecklistCompletionRequest(
    @field:NotNull(message = "Completed is required")
    val completed: Boolean,
)

data class ChecklistItemResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val completed: Boolean,
    val completedAt: java.time.Instant?,
)

enum class ChecklistStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
}

data class ChecklistResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val frequency: ChecklistFrequency,
    val active: Boolean,
    val itemCount: Int,
    val completedItemCount: Int,
    val status: ChecklistStatus,
    val items: List<ChecklistItemResponse>,
)

data class ChecklistStatsResponse(
    val activeChecklists: Long,
    val totalChecklistItems: Long,
)
