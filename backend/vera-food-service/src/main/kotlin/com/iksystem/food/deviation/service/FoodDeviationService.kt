package com.iksystem.food.deviation.service

import com.iksystem.food.deviation.dto.CreateFoodDeviationRequest
import com.iksystem.food.deviation.dto.FoodDeviationResponse
import com.iksystem.food.deviation.dto.UpdateFoodDeviationRequest
import com.iksystem.food.deviation.model.FoodDeviation
import com.iksystem.food.deviation.model.FoodDeviationStatus
import com.iksystem.food.deviation.repository.FoodDeviationRepository
import com.iksystem.common.exception.BadRequestException
import com.iksystem.common.exception.NotFoundException
import com.iksystem.common.membership.repository.MembershipRepository
import com.iksystem.common.notifications.model.NotificationType
import com.iksystem.common.notifications.model.ReferenceType
import com.iksystem.common.notifications.service.NotificationsService
import com.iksystem.common.security.AuthenticatedUser
import com.iksystem.common.user.model.User
import com.iksystem.common.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class FoodDeviationService(
    private val repository: FoodDeviationRepository,
    private val userRepository: UserRepository,
    private val membershipRepository: MembershipRepository,
    private val notificationsService: NotificationsService,
) {

    @Transactional(readOnly = true)
    fun list(auth: AuthenticatedUser): List<FoodDeviationResponse> {
        val orgId = auth.requireOrganizationId()
        return repository.findAllByOrganizationIdOrderByReportedAtDesc(orgId).map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getById(id: Long, auth: AuthenticatedUser): FoodDeviationResponse {
        val orgId = auth.requireOrganizationId()
        return requireDeviation(id, orgId).toResponse()
    }

    @Transactional
    fun create(request: CreateFoodDeviationRequest, auth: AuthenticatedUser): FoodDeviationResponse {
        val orgId = auth.requireOrganizationId()
        val reporter = requireUser(auth.userId)
        val actionByUser = resolveOrgMember(request.immediateActionByUserId, orgId)
        val responsibleUser = resolveOrgMember(request.preventiveResponsibleUserId, orgId)

        val deviation = repository.save(
            FoodDeviation(
                organizationId = orgId,
                reportedAt = request.reportedAt?.let { Instant.parse(it) } ?: Instant.now(),
                reportedByUser = reporter,
                deviationType = request.deviationType,
                severity = request.severity,
                description = request.description.trim(),
                immediateAction = request.immediateAction?.trim()?.takeIf { it.isNotEmpty() },
                immediateActionByUser = actionByUser,
                immediateActionAt = request.immediateActionAt?.let { Instant.parse(it) },
                cause = request.cause?.trim()?.takeIf { it.isNotEmpty() },
                preventiveMeasures = request.preventiveMeasures?.trim()?.takeIf { it.isNotEmpty() },
                preventiveResponsibleUser = responsibleUser,
                preventiveDeadline = request.preventiveDeadline?.let { Instant.parse(it) },
            )
        )
        notifyDeviationCreated(deviation)

        return deviation.toResponse()
    }

    @Transactional
    fun update(id: Long, request: UpdateFoodDeviationRequest, auth: AuthenticatedUser): FoodDeviationResponse {
        val orgId = auth.requireOrganizationId()
        val existing = requireDeviation(id, orgId)

        val actionByUser = if (request.immediateActionByUserId != null)
            resolveOrgMember(request.immediateActionByUserId, orgId) else existing.immediateActionByUser
        val responsibleUser = if (request.preventiveResponsibleUserId != null)
            resolveOrgMember(request.preventiveResponsibleUserId, orgId) else existing.preventiveResponsibleUser

        val updated = repository.save(
            existing.copy(
                reportedAt = request.reportedAt?.let { Instant.parse(it) } ?: existing.reportedAt,
                deviationType = request.deviationType ?: existing.deviationType,
                severity = request.severity ?: existing.severity,
                description = request.description?.trim()?.takeIf { it.isNotEmpty() } ?: existing.description,
                immediateAction = request.immediateAction?.trim()?.takeIf { it.isNotEmpty() } ?: existing.immediateAction,
                immediateActionByUser = actionByUser,
                immediateActionAt = request.immediateActionAt?.let { Instant.parse(it) } ?: existing.immediateActionAt,
                cause = request.cause?.trim()?.takeIf { it.isNotEmpty() } ?: existing.cause,
                preventiveMeasures = request.preventiveMeasures?.trim()?.takeIf { it.isNotEmpty() } ?: existing.preventiveMeasures,
                preventiveResponsibleUser = responsibleUser,
                preventiveDeadline = request.preventiveDeadline?.let { Instant.parse(it) } ?: existing.preventiveDeadline,
                status = request.status ?: existing.status,
                updatedAt = Instant.now(),
            )
        )
        return updated.toResponse()
    }

    @Transactional
    fun delete(id: Long, auth: AuthenticatedUser) {
        val orgId = auth.requireOrganizationId()
        val deviation = requireDeviation(id, orgId)
        repository.delete(deviation)
    }

    private fun notifyDeviationCreated(deviation: FoodDeviation) {
        val message = "Food deviation '${deviation.deviationType}' (${deviation.severity}) reported by ${deviation.reportedByUser.fullName}: ${deviation.description.take(100)}"

        notificationsService.sendToOrgAdminsAndManagers(
            organizationId = deviation.organizationId,
            type = NotificationType.DEVIATION_CREATED,
            title = "New Food Deviation: ${deviation.deviationType}",
            message = message,
            referenceType = ReferenceType.DEVIATION,
            referenceId = deviation.id
        )

        val responsibleUser = deviation.preventiveResponsibleUser
        if (responsibleUser != null) {
            notificationsService.send(
                organizationId = deviation.organizationId,
                recipientUserId = responsibleUser.id,
                type = NotificationType.DEVIATION_ASSIGNED,
                title = "You have been assigned a deviation",
                message = message,
                referenceType = ReferenceType.DEVIATION,
                referenceId = deviation.id
            )
        }
    }

    private fun requireDeviation(id: Long, organizationId: Long): FoodDeviation {
        return repository.findByIdAndOrganizationId(id, organizationId)
            ?: throw NotFoundException("Food deviation not found")
    }

    private fun requireUser(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { NotFoundException("User not found") }
    }

    private fun resolveOrgMember(userId: Long?, organizationId: Long): User? {
        if (userId == null) return null
        val user = requireUser(userId)
        if (!membershipRepository.existsByUserIdAndOrganizationId(user.id, organizationId)) {
            throw BadRequestException("User is not a member of this organization")
        }
        return user
    }
}

private fun FoodDeviation.toResponse() = FoodDeviationResponse(
    id = id,
    organizationId = organizationId,
    reportedAt = reportedAt.toString(),
    reportedByUserId = reportedByUser.id,
    reportedByUserName = reportedByUser.fullName,
    deviationType = deviationType,
    severity = severity,
    description = description,
    immediateAction = immediateAction,
    immediateActionByUserId = immediateActionByUser?.id,
    immediateActionByUserName = immediateActionByUser?.fullName,
    immediateActionAt = immediateActionAt?.toString(),
    cause = cause,
    preventiveMeasures = preventiveMeasures,
    preventiveResponsibleUserId = preventiveResponsibleUser?.id,
    preventiveResponsibleUserName = preventiveResponsibleUser?.fullName,
    preventiveDeadline = preventiveDeadline?.toString(),
    status = status,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt.toString(),
)
