package com.iksystem.alcohol.penaltypoints.service

import com.iksystem.alcohol.deviation.model.AlcoholDeviation
import com.iksystem.alcohol.penaltypoints.dto.CreatePenaltyPointRequest
import com.iksystem.alcohol.penaltypoints.dto.PenaltyPointResponse
import com.iksystem.alcohol.penaltypoints.dto.PenaltyPointSummaryResponse
import com.iksystem.alcohol.penaltypoints.model.PenaltyPoint
import com.iksystem.alcohol.penaltypoints.repository.PenaltyPointRepository
import com.iksystem.common.exception.NotFoundException
import com.iksystem.common.security.AuthenticatedUser
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PenaltyPointService(
    private val repository: PenaltyPointRepository,
) {

    @Transactional(readOnly = true)
    fun getSummary(auth: AuthenticatedUser): PenaltyPointSummaryResponse {
        val orgId = auth.requireOrganizationId()
        val entries = repository.findAllByOrganizationIdOrderByCreatedAtDesc(orgId)
        val total = repository.sumPointsByOrganizationId(orgId)
        return PenaltyPointSummaryResponse(
            organizationId = orgId,
            totalPoints = total,
            entries = entries.map { it.toResponse() },
        )
    }

    @Transactional
    fun add(request: CreatePenaltyPointRequest, auth: AuthenticatedUser): PenaltyPointResponse {
        val orgId = auth.requireOrganizationId()
        val entry = repository.save(
            PenaltyPoint(
                organizationId = orgId,
                points = request.violationType.penaltyPoints,
                violationType = request.violationType,
                description = request.description?.trim()?.takeIf { it.isNotEmpty() },
            )
        )
        return entry.toResponse()
    }

    @Transactional
    fun delete(id: Long, auth: AuthenticatedUser) {
        val orgId = auth.requireOrganizationId()
        val entry = repository.findByIdAndOrganizationId(id, orgId)
            ?: throw NotFoundException("Penalty point entry not found")
        repository.delete(entry)
    }

    /**
     * Auto-creates a penalty point entry linked to an alcohol deviation.
     * Called internally when a deviation with source SJENKEKONTROLL or POLITIRAPPORT is created.
     */
    @Transactional
    fun addForDeviation(deviation: AlcoholDeviation) {
        repository.save(
            PenaltyPoint(
                organizationId = deviation.organizationId,
                alcoholDeviation = deviation,
                points = deviation.deviationType.penaltyPoints,
                violationType = deviation.deviationType,
                description = "Auto: ${deviation.reportSource.name.lowercase()} – ${deviation.deviationType.name}",
            )
        )
    }
}

private fun PenaltyPoint.toResponse() = PenaltyPointResponse(
    id = id,
    organizationId = organizationId,
    alcoholDeviationId = alcoholDeviation?.id,
    points = points,
    violationType = violationType,
    description = description,
    createdAt = createdAt.toString(),
)