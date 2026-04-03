package com.iksystem.common.deviation.alcohol.dto

import com.iksystem.common.deviation.alcohol.model.AlcoholCausalAnalysis
import com.iksystem.common.deviation.alcohol.model.AlcoholDeviationStatus
import com.iksystem.common.deviation.alcohol.model.AlcoholDeviationType
import com.iksystem.common.deviation.alcohol.model.AlcoholReportSource
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateAlcoholDeviationRequest(
    val reportedAt: String? = null,

    @field:NotNull(message = "Report source is required")
    val reportSource: AlcoholReportSource,

    @field:NotNull(message = "Deviation type is required")
    val deviationType: AlcoholDeviationType,

    @field:NotBlank(message = "Description is required")
    @field:Size(max = 5000)
    val description: String,

    @field:Size(max = 5000)
    val immediateAction: String? = null,

    val causalAnalysis: AlcoholCausalAnalysis? = null,

    @field:Size(max = 5000)
    val causalExplanation: String? = null,

    @field:Size(max = 5000)
    val preventiveMeasures: String? = null,

    val preventiveDeadline: String? = null,
    val preventiveResponsibleUserId: Long? = null,
)

data class UpdateAlcoholDeviationRequest(
    val reportedAt: String? = null,
    val reportSource: AlcoholReportSource? = null,
    val deviationType: AlcoholDeviationType? = null,

    @field:Size(max = 5000)
    val description: String? = null,

    @field:Size(max = 5000)
    val immediateAction: String? = null,

    val causalAnalysis: AlcoholCausalAnalysis? = null,

    @field:Size(max = 5000)
    val causalExplanation: String? = null,

    @field:Size(max = 5000)
    val preventiveMeasures: String? = null,

    val preventiveDeadline: String? = null,
    val preventiveResponsibleUserId: Long? = null,
    val status: AlcoholDeviationStatus? = null,
)

data class AlcoholDeviationResponse(
    val id: Long,
    val organizationId: Long,
    val reportedAt: String,
    val reportedByUserId: Long,
    val reportedByUserName: String,
    val reportSource: AlcoholReportSource,
    val deviationType: AlcoholDeviationType,
    val description: String,
    val immediateAction: String?,
    val causalAnalysis: AlcoholCausalAnalysis?,
    val causalExplanation: String?,
    val preventiveMeasures: String?,
    val preventiveDeadline: String?,
    val preventiveResponsibleUserId: Long?,
    val preventiveResponsibleUserName: String?,
    val status: AlcoholDeviationStatus,
    val createdAt: String,
    val updatedAt: String,
)
