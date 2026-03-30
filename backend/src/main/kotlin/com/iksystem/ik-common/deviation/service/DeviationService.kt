package com.iksystem.`ik-common`.deviation.service

import com.ik.ikcommon.exception.BadRequestException
import com.ik.ikcommon.exception.NotFoundException
import com.iksystem.`ik-common`.deviation.dto.CreateDeviationRequest
import com.iksystem.`ik-common`.deviation.dto.DeviationResponse
import com.iksystem.`ik-common`.deviation.dto.UpdateDeviationRequest
import com.iksystem.`ik-common`.deviation.dto.UpdateDeviationStatusRequest
import com.iksystem.`ik-common`.deviation.model.Deviation
import com.iksystem.`ik-common`.deviation.model.DeviationStatus
import com.iksystem.`ik-common`.deviation.repository.DeviationRepository
import com.iksystem.`ik-common`.membership.repository.MembershipRepository
import com.iksystem.`ik-common`.security.AuthenticatedUser
import com.iksystem.`ik-common`.user.model.User
import com.iksystem.`ik-common`.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class DeviationService(
    private val deviationRepository: DeviationRepository,
    private val userRepository: UserRepository,
    private val membershipRepository: MembershipRepository,
) {

    @Transactional(readOnly = true)
    fun list(auth: AuthenticatedUser): List<DeviationResponse> {
        val orgId = auth.requireOrganizationId()
        return deviationRepository.findAllByOrganizationIdOrderByReportedAtDesc(orgId).map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getById(id: Long, auth: AuthenticatedUser): DeviationResponse {
        val orgId = auth.requireOrganizationId()
        val deviation = requireDeviation(id, orgId)
        return deviation.toResponse()
    }

    @Transactional
    fun create(request: CreateDeviationRequest, auth: AuthenticatedUser): DeviationResponse {
        val orgId = auth.requireOrganizationId()
        val reporter = requireUser(auth.userId)
        val assignee = resolveAssignee(request.assignedToUserId, orgId)

        val deviation = deviationRepository.save(
            Deviation(
                organizationId = orgId,
                module = request.module,
                title = request.title.trim(),
                description = request.description.trim(),
                immediateAction = request.immediateAction?.trim()?.takeIf { it.isNotEmpty() },
                severity = request.severity,
                status = DeviationStatus.OPEN,
                reportedByUser = reporter,
                assignedToUser = assignee,
                reportedAt = Instant.now(),
            )
        )

        return deviation.toResponse()
    }

    @Transactional
    fun update(id: Long, request: UpdateDeviationRequest, auth: AuthenticatedUser): DeviationResponse {
        val orgId = auth.requireOrganizationId()
        val existing = requireDeviation(id, orgId)

        if (
            request.module == null &&
            request.title == null &&
            request.description == null &&
            request.immediateAction == null &&
            request.severity == null &&
            request.assignedToUserId == null
        ) {
            throw BadRequestException("No fields provided to update")
        }

        val assignee =
            if (request.assignedToUserId != null) {
                resolveAssignee(request.assignedToUserId, orgId)
            } else {
                existing.assignedToUser
            }

        val updated = deviationRepository.save(
            existing.copy(
                module = request.module ?: existing.module,
                title = request.title?.trim()?.takeIf { it.isNotEmpty() } ?: existing.title,
                description = request.description?.trim()?.takeIf { it.isNotEmpty() } ?: existing.description,
                immediateAction = request.immediateAction?.trim()?.takeIf { it.isNotEmpty() } ?: existing.immediateAction,
                severity = request.severity ?: existing.severity,
                assignedToUser = assignee,
                updatedAt = Instant.now(),
            )
        )

        return updated.toResponse()
    }

    @Transactional
    fun updateStatus(id: Long, request: UpdateDeviationStatusRequest, auth: AuthenticatedUser): DeviationResponse {
        val orgId = auth.requireOrganizationId()
        val existing = requireDeviation(id, orgId)
        val now = Instant.now()

        val resolvedAt = when (request.status) {
            DeviationStatus.RESOLVED -> existing.resolvedAt ?: now
            DeviationStatus.CLOSED -> existing.resolvedAt ?: now
            else -> null
        }

        val closedAt = when (request.status) {
            DeviationStatus.CLOSED -> existing.closedAt ?: now
            else -> null
        }

        val updated = deviationRepository.save(
            existing.copy(
                status = request.status,
                resolvedAt = resolvedAt,
                closedAt = closedAt,
                updatedAt = now,
            )
        )

        return updated.toResponse()
    }

    @Transactional
    fun delete(id: Long, auth: AuthenticatedUser) {
        val orgId = auth.requireOrganizationId()
        val deviation = requireDeviation(id, orgId)
        deviationRepository.delete(deviation)
    }

    private fun requireDeviation(id: Long, organizationId: Long): Deviation {
        return deviationRepository.findByIdAndOrganizationId(id, organizationId)
            ?: throw NotFoundException("Deviation not found")
    }

    private fun requireUser(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { NotFoundException("User not found") }
    }

    private fun resolveAssignee(assigneeUserId: Long?, organizationId: Long): User? {
        if (assigneeUserId == null) {
            return null
        }

        val assignee = requireUser(assigneeUserId)
        val isMember = membershipRepository.existsByUserIdAndOrganizationId(assignee.id, organizationId)
        if (!isMember) {
            throw BadRequestException("Assigned user is not a member of this organization")
        }

        return assignee
    }
}

private fun Deviation.toResponse(): DeviationResponse = DeviationResponse(
    id = id,
    organizationId = organizationId,
    module = module,
    title = title,
    description = description,
    immediateAction = immediateAction,
    severity = severity,
    status = status,
    reportedByUserId = reportedByUser.id,
    reportedByUserName = reportedByUser.fullName,
    assignedToUserId = assignedToUser?.id,
    assignedToUserName = assignedToUser?.fullName,
    reportedAt = reportedAt.toString(),
    resolvedAt = resolvedAt?.toString(),
    closedAt = closedAt?.toString(),
    createdAt = createdAt.toString(),
    updatedAt = updatedAt.toString(),
)
