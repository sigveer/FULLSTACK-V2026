package com.iksystem.common.deviation.food.dto

import com.iksystem.common.deviation.food.model.FoodDeviationStatus
import com.iksystem.common.deviation.food.model.FoodDeviationType
import com.iksystem.common.deviation.model.DeviationSeverity
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateFoodDeviationRequest(
    val reportedAt: String? = null,

    @field:NotNull(message = "Deviation type is required")
    val deviationType: FoodDeviationType,

    @field:NotNull(message = "Severity is required")
    val severity: DeviationSeverity,

    @field:NotBlank(message = "Description is required")
    @field:Size(max = 5000)
    val description: String,

    @field:Size(max = 5000)
    val immediateAction: String? = null,

    val immediateActionByUserId: Long? = null,
    val immediateActionAt: String? = null,

    @field:Size(max = 5000)
    val cause: String? = null,

    @field:Size(max = 5000)
    val preventiveMeasures: String? = null,

    val preventiveResponsibleUserId: Long? = null,
    val preventiveDeadline: String? = null,
)

data class UpdateFoodDeviationRequest(
    val reportedAt: String? = null,
    val deviationType: FoodDeviationType? = null,
    val severity: DeviationSeverity? = null,

    @field:Size(max = 5000)
    val description: String? = null,

    @field:Size(max = 5000)
    val immediateAction: String? = null,

    val immediateActionByUserId: Long? = null,
    val immediateActionAt: String? = null,

    @field:Size(max = 5000)
    val cause: String? = null,

    @field:Size(max = 5000)
    val preventiveMeasures: String? = null,

    val preventiveResponsibleUserId: Long? = null,
    val preventiveDeadline: String? = null,
    val status: FoodDeviationStatus? = null,
)

data class FoodDeviationResponse(
    val id: Long,
    val organizationId: Long,
    val reportedAt: String,
    val reportedByUserId: Long,
    val reportedByUserName: String,
    val deviationType: FoodDeviationType,
    val severity: DeviationSeverity,
    val description: String,
    val immediateAction: String?,
    val immediateActionByUserId: Long?,
    val immediateActionByUserName: String?,
    val immediateActionAt: String?,
    val cause: String?,
    val preventiveMeasures: String?,
    val preventiveResponsibleUserId: Long?,
    val preventiveResponsibleUserName: String?,
    val preventiveDeadline: String?,
    val status: FoodDeviationStatus,
    val createdAt: String,
    val updatedAt: String,
)
