package com.iksystem.common.notifications.controller

import com.iksystem.common.notifications.dto.UnreadCountResponse
import com.iksystem.common.notifications.dto.NotificationsResponse
import com.iksystem.common.notifications.model.Notification
import com.iksystem.common.notifications.repository.NotificationsRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import com.iksystem.common.security.AuthenticatedUser
import jakarta.transaction.Transactional
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("api/v1/notifications")
class NotificationsController(private val notificationsRepository: NotificationsRepository) {
    @GetMapping
    fun list(
        @AuthenticationPrincipal auth: AuthenticatedUser,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): ResponseEntity<Page<NotificationsResponse>> {
        val orgId = auth.requireOrganizationId()
        val pageable = PageRequest.of(page, size)
        val notifications = notificationsRepository
            .findAllByRecipientUserIdAndOrganizationIdOrderByCreatedAtDesc(auth.userId, orgId, pageable)
            .map { it.toResponse() }
        return ResponseEntity.ok(notifications)
    }

    @GetMapping("/unread-count")
    fun getUnreadCount(@AuthenticationPrincipal auth: AuthenticatedUser): ResponseEntity<UnreadCountResponse> {
        val orgId = auth.requireOrganizationId()
        val count = notificationsRepository.countByRecipientUserIdAndOrganizationIdAndIsReadFalse(auth.userId, orgId)
        return ResponseEntity.ok(UnreadCountResponse(count))
    }

    @PatchMapping("/{id}/read")
    @Transactional
    fun markRead(
        @PathVariable id: Long,
        @AuthenticationPrincipal auth: AuthenticatedUser
    ): ResponseEntity<Void> {
        val notif = notificationsRepository.findById(id).orElse(null)
        if (notif != null && notif.recipientUserId == auth.userId && notif.organizationId == auth.requireOrganizationId()) {
            notif.isRead = true
            notif.readAt = Instant.now()
            notificationsRepository.save(notif)
        }
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/mark-all-read")
    @Transactional
    fun markAllRead(@AuthenticationPrincipal auth: AuthenticatedUser): ResponseEntity<Void> {
        notificationsRepository.markAllAsRead(auth.userId, auth.requireOrganizationId())
        return ResponseEntity.noContent().build()
    }

    private fun Notification.toResponse() = NotificationsResponse(
        id = id,
        title = title,
        message = message,
        type = type,
        referenceType = referenceType,
        referenceId = referenceId,
        isRead = isRead,
        createdAt = createdAt.toString()
    )
}
