package com.iksystem.alcohol.penaltypoints.dto

import com.iksystem.alcohol.deviation.model.AlcoholDeviationType
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreatePenaltyPointRequest(
    @field:NotNull(message = "Violation type is required")
    val violationType: AlcoholDeviationType,

    @field:Size(max = 500)
    val description: String? = null,
)

data class PenaltyPointResponse(
    val id: Long,
    val organizationId: Long,
    val alcoholDeviationId: Long?,
    val points: Int,
    val violationType: AlcoholDeviationType,
    val description: String?,
    val createdAt: String,
)

data class PenaltyPointSummaryResponse(
    val organizationId: Long,
    val totalPoints: Int,
    val entries: List<PenaltyPointResponse>,
)