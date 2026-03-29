package com.iksystem.`ik-common`.checklist.service

import com.ik.ikcommon.exception.BadRequestException
import com.ik.ikcommon.exception.NotFoundException
import com.iksystem.`ik-common`.checklist.dto.ChecklistItemResponse
import com.iksystem.`ik-common`.checklist.dto.ChecklistResponse
import com.iksystem.`ik-common`.checklist.dto.ChecklistStatus
import com.iksystem.`ik-common`.checklist.dto.ChecklistStatsResponse
import com.iksystem.`ik-common`.checklist.dto.CreateChecklistItemRequest
import com.iksystem.`ik-common`.checklist.dto.CreateChecklistRequest
import com.iksystem.`ik-common`.checklist.dto.SetChecklistCompletionRequest
import com.iksystem.`ik-common`.checklist.dto.UpdateChecklistItemRequest
import com.iksystem.`ik-common`.checklist.dto.UpdateChecklistRequest
import com.iksystem.`ik-common`.checklist.model.Checklist
import com.iksystem.`ik-common`.checklist.model.ChecklistItem
import com.iksystem.`ik-common`.checklist.repository.ChecklistItemRepository
import com.iksystem.`ik-common`.checklist.repository.ChecklistRepository
import com.iksystem.`ik-common`.security.AuthenticatedUser
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class ChecklistService(
    private val checklistRepository: ChecklistRepository,
    private val checklistItemRepository: ChecklistItemRepository,
) {

    @Transactional(readOnly = true)
    fun list(auth: AuthenticatedUser): List<ChecklistResponse> {
        val orgId = auth.requireOrganizationId()
        val checklists = checklistRepository.findAllByOrganizationIdOrderByCreatedAtDesc(orgId)

        return checklists.map { checklist ->
            val items = checklistItemRepository.findAllByChecklistIdOrderByIdAsc(checklist.id)
            checklist.toResponse(items)
        }
    }

    @Transactional(readOnly = true)
    fun stats(auth: AuthenticatedUser): ChecklistStatsResponse {
        val orgId = auth.requireOrganizationId()
        return ChecklistStatsResponse(
            activeChecklists = checklistRepository.countActiveOpenByOrganizationId(orgId),
            totalChecklistItems = checklistItemRepository.countAllByOrganizationId(orgId),
        )
    }

    @Transactional
    fun createChecklist(request: CreateChecklistRequest, auth: AuthenticatedUser): ChecklistResponse {
        val orgId = auth.requireOrganizationId()
        val checklist = checklistRepository.save(
            Checklist(
                organizationId = orgId,
                name = request.name.trim(),
                description = request.description?.trim()?.takeIf { it.isNotEmpty() },
                frequency = request.frequency,
            )
        )
        return checklist.toResponse(emptyList())
    }

    @Transactional
    fun updateChecklist(checklistId: Long, request: UpdateChecklistRequest, auth: AuthenticatedUser): ChecklistResponse {
        val orgId = auth.requireOrganizationId()
        val checklist = requireChecklist(checklistId, orgId)

        if (
            request.name == null &&
            request.description == null &&
            request.frequency == null &&
            request.active == null
        ) {
            throw BadRequestException("No fields provided to update")
        }

        val updated = checklistRepository.save(
            checklist.copy(
                name = request.name?.trim()?.takeIf { it.isNotEmpty() } ?: checklist.name,
                description = request.description?.trim()?.takeIf { it.isNotEmpty() } ?: checklist.description,
                frequency = request.frequency ?: checklist.frequency,
                active = request.active ?: checklist.active,
                updatedAt = Instant.now(),
            )
        )

        val items = checklistItemRepository.findAllByChecklistIdOrderByIdAsc(updated.id)
        return updated.toResponse(items)
    }

    @Transactional
    fun deleteChecklist(checklistId: Long, auth: AuthenticatedUser) {
        val orgId = auth.requireOrganizationId()
        val checklist = requireChecklist(checklistId, orgId)
        checklistRepository.delete(checklist)
    }

    @Transactional
    fun createItem(checklistId: Long, request: CreateChecklistItemRequest, auth: AuthenticatedUser): ChecklistItemResponse {
        val orgId = auth.requireOrganizationId()
        val checklist = requireChecklist(checklistId, orgId)

        val item = checklistItemRepository.save(
            ChecklistItem(
                checklist = checklist,
                title = request.title.trim(),
                description = request.description?.trim()?.takeIf { it.isNotEmpty() },
                completed = request.completed,
                completedAt = if (request.completed) Instant.now() else null,
            )
        )

        return item.toResponse()
    }

    @Transactional
    fun updateItem(
        checklistId: Long,
        itemId: Long,
        request: UpdateChecklistItemRequest,
        auth: AuthenticatedUser,
    ): ChecklistItemResponse {
        val orgId = auth.requireOrganizationId()
        requireChecklist(checklistId, orgId)

        if (request.title == null && request.description == null && request.completed == null) {
            throw BadRequestException("No fields provided to update")
        }

        val item = checklistItemRepository.findByIdAndChecklistId(itemId, checklistId)
            ?: throw NotFoundException("Checklist item not found")

        val updated = checklistItemRepository.save(
            item.copy(
                title = request.title?.trim()?.takeIf { it.isNotEmpty() } ?: item.title,
                description = request.description?.trim()?.takeIf { it.isNotEmpty() } ?: item.description,
                completed = request.completed ?: item.completed,
                completedAt = when (request.completed) {
                    true -> item.completedAt ?: Instant.now()
                    false -> null
                    null -> item.completedAt
                },
                updatedAt = Instant.now(),
            )
        )

        return updated.toResponse()
    }

    @Transactional
    fun deleteItem(checklistId: Long, itemId: Long, auth: AuthenticatedUser) {
        val orgId = auth.requireOrganizationId()
        requireChecklist(checklistId, orgId)

        val item = checklistItemRepository.findByIdAndChecklistId(itemId, checklistId)
            ?: throw NotFoundException("Checklist item not found")

        checklistItemRepository.delete(item)
    }

    @Transactional
    fun setChecklistCompletion(
        checklistId: Long,
        request: SetChecklistCompletionRequest,
        auth: AuthenticatedUser,
    ): ChecklistResponse {
        val orgId = auth.requireOrganizationId()
        val checklist = requireChecklist(checklistId, orgId)

        val now = Instant.now()
        checklistItemRepository.setChecklistCompletion(
            checklistId = checklist.id,
            completed = request.completed,
            completedAt = if (request.completed) now else null,
            updatedAt = now,
        )

        val items = checklistItemRepository.findAllByChecklistIdOrderByIdAsc(checklist.id)
        return checklist.toResponse(items)
    }

    private fun requireChecklist(checklistId: Long, organizationId: Long): Checklist {
        return checklistRepository.findByIdAndOrganizationId(checklistId, organizationId)
            ?: throw NotFoundException("Checklist not found")
    }
}

private fun Checklist.toResponse(items: List<ChecklistItem>): ChecklistResponse = ChecklistResponse(
    id = id,
    name = name,
    description = description,
    frequency = frequency,
    active = active,
    itemCount = items.size,
    completedItemCount = items.count { it.completed },
    status = items.toChecklistStatus(),
    items = items.map { it.toResponse() },
)

private fun ChecklistItem.toResponse(): ChecklistItemResponse = ChecklistItemResponse(
    id = id,
    title = title,
    description = description,
    completed = completed,
)

private fun List<ChecklistItem>.toChecklistStatus(): ChecklistStatus {
    if (isEmpty()) {
        return ChecklistStatus.NOT_STARTED
    }

    val completedCount = count { it.completed }
    return when {
        completedCount == 0 -> ChecklistStatus.NOT_STARTED
        completedCount == size -> ChecklistStatus.COMPLETED
        else -> ChecklistStatus.IN_PROGRESS
    }
}
