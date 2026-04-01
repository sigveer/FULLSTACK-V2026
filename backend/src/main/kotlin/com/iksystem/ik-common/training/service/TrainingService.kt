package com.iksystem.`ik-common`.training.service

import com.ik.ikcommon.exception.BadRequestException
import com.ik.ikcommon.exception.NotFoundException
import com.iksystem.`ik-common`.membership.repository.MembershipRepository
import com.iksystem.`ik-common`.security.AuthenticatedUser
import com.iksystem.`ik-common`.training.dto.CreateTrainingLogRequest
import com.iksystem.`ik-common`.training.dto.TrainingLogResponse
import com.iksystem.`ik-common`.training.dto.UpdateTrainingLogRequest
import com.iksystem.`ik-common`.training.model.TrainingLog
import com.iksystem.`ik-common`.training.repository.TrainingRepository
import com.iksystem.`ik-common`.user.model.User
import com.iksystem.`ik-common`.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class TrainingService(
    private val trainingRepository: TrainingRepository,
    private val userRepository: UserRepository,
    private val membershipRepository: MembershipRepository,
) {

    @Transactional(readOnly = true)
    fun list(auth: AuthenticatedUser): List<TrainingLogResponse> {
        val orgId = auth.requireOrganizationId()
        return trainingRepository.findAllByOrganizationIdOrderByCreatedAtDesc(orgId)
            .map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getById(id: Long, auth: AuthenticatedUser): TrainingLogResponse {
        val orgId = auth.requireOrganizationId()
        return requireTrainingLog(id, orgId).toResponse()
    }

    @Transactional
    fun create(request: CreateTrainingLogRequest, auth: AuthenticatedUser): TrainingLogResponse {
        val orgId = auth.requireOrganizationId()
        val employee = requireMember(request.employeeUserId, orgId)
        val loggedBy = requireUser(auth.userId)

        val trainingLog = trainingRepository.save(
            TrainingLog(
                organizationId = orgId,
                employeeUser = employee,
                loggedByUser = loggedBy,
                title = request.title.trim(),
                description = request.description?.trim()?.takeIf { it.isNotEmpty() },
                completedAt = request.completedAt?.let { Instant.parse(it) },
                expiresAt = request.expiresAt?.let { Instant.parse(it) },
                status = request.status,
            )
        )

        return trainingLog.toResponse()
    }

    @Transactional
    fun update(id: Long, request: UpdateTrainingLogRequest, auth: AuthenticatedUser): TrainingLogResponse {
        val orgId = auth.requireOrganizationId()
        val existing = requireTrainingLog(id, orgId)

        if (
            request.employeeUserId == null &&
            request.title == null &&
            request.description == null &&
            request.completedAt == null &&
            request.expiresAt == null &&
            request.status == null
        ) {
            throw BadRequestException("No fields provided to update")
        }

        val employee = if (request.employeeUserId != null) {
            requireMember(request.employeeUserId, orgId)
        } else {
            existing.employeeUser
        }

        val updated = trainingRepository.save(
            existing.copy(
                employeeUser = employee,
                title = request.title?.trim()?.takeIf { it.isNotEmpty() } ?: existing.title,
                description = request.description?.trim()?.takeIf { it.isNotEmpty() } ?: existing.description,
                completedAt = request.completedAt?.let { Instant.parse(it) } ?: existing.completedAt,
                expiresAt = request.expiresAt?.let { Instant.parse(it) } ?: existing.expiresAt,
                status = request.status ?: existing.status,
                updatedAt = Instant.now(),
            )
        )

        return updated.toResponse()
    }

    @Transactional
    fun delete(id: Long, auth: AuthenticatedUser) {
        val orgId = auth.requireOrganizationId()
        val trainingLog = requireTrainingLog(id, orgId)
        trainingRepository.delete(trainingLog)
    }

    private fun requireTrainingLog(id: Long, organizationId: Long): TrainingLog {
        return trainingRepository.findByIdAndOrganizationId(id, organizationId)
            ?: throw NotFoundException("Training log not found")
    }

    private fun requireUser(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { NotFoundException("User not found") }
    }

    private fun requireMember(userId: Long, organizationId: Long): User {
        val user = requireUser(userId)
        val isMember = membershipRepository.existsByUserIdAndOrganizationId(user.id, organizationId)
        if (!isMember) {
            throw BadRequestException("Employee is not a member of this organization")
        }
        return user
    }
}

private fun TrainingLog.toResponse(): TrainingLogResponse = TrainingLogResponse(
    id = id,
    organizationId = organizationId,
    employeeUserId = employeeUser.id,
    employeeUserName = employeeUser.fullName,
    loggedByUserId = loggedByUser.id,
    loggedByUserName = loggedByUser.fullName,
    title = title,
    description = description,
    completedAt = completedAt?.toString(),
    expiresAt = expiresAt?.toString(),
    status = status,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt.toString(),
)
