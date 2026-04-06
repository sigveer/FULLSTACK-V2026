package com.iksystem.common.notifications.service

import com.iksystem.common.membership.repository.MembershipRepository
import com.iksystem.common.notifications.model.Notification
import com.iksystem.common.notifications.model.NotificationType
import com.iksystem.common.notifications.model.ReferenceType
import com.iksystem.common.notifications.repository.NotificationsRepository
import com.iksystem.common.resend.service.ResendService
import com.iksystem.common.user.model.Role
import com.iksystem.common.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class NotificationsService(
    private val notificationsRepository: NotificationsRepository,
    private val userRepository: UserRepository,
    private val membershipRepository: MembershipRepository,
    private val resendService: ResendService
) {
    private val log = LoggerFactory.getLogger(NotificationsService::class.java)

    @Transactional
    fun send(
        organizationId: Long,
        recipientUserId: Long,
        type: NotificationType,
        title: String,
        message: String,
        referenceType: ReferenceType? = null,
        referenceId: Long? = null,
        sendEmail: Boolean = true
    ) {
        val notification = notificationsRepository.save(
            Notification(
                organizationId = organizationId,
                recipientUserId = recipientUserId,
                type = type,
                title = title,
                message = message,
                referenceType = referenceType,
                referenceId = referenceId
            )
        )

        if (sendEmail) {
            val user = userRepository.findById(recipientUserId).orElse(null)
            if (user != null && user.email.isNotEmpty()) {
                try {
                    resendService.sendNotificationEmail(user.email, title, message)
                    notification.emailSent = true
                    notificationsRepository.save(notification)
                } catch (e: Exception) {
                    log.error("Failed to send notification email to user {}: {}", recipientUserId, e.message)
                }
            }
        }
    }

    @Transactional
    fun sendToOrgAdminsAndManagers(
        organizationId: Long,
        type: NotificationType,
        title: String,
        message: String,
        referenceType: ReferenceType? = null,
        referenceId: Long? = null,
        sendEmail: Boolean = true
    ) {
        sendToRoles(organizationId, setOf(Role.ADMIN, Role.MANAGER), type, title, message, referenceType, referenceId, sendEmail)
    }

    @Transactional
    fun sendToOrgAdmins(
        organizationId: Long,
        type: NotificationType,
        title: String,
        message: String,
        referenceType: ReferenceType? = null,
        referenceId: Long? = null,
        sendEmail: Boolean = true
    ) {
        sendToRoles(organizationId, setOf(Role.ADMIN), type, title, message, referenceType, referenceId, sendEmail)
    }

    @Transactional
    fun sendToOrgManagers(
        organizationId: Long,
        type: NotificationType,
        title: String,
        message: String,
        referenceType: ReferenceType? = null,
        referenceId: Long? = null,
        sendEmail: Boolean = true
    ) {
        sendToRoles(organizationId, setOf(Role.MANAGER), type, title, message, referenceType, referenceId, sendEmail)
    }

    private fun sendToRoles(
        organizationId: Long,
        roles: Set<Role>,
        type: NotificationType,
        title: String,
        message: String,
        referenceType: ReferenceType?,
        referenceId: Long?,
        sendEmail: Boolean
    ) {
        val members = membershipRepository.findAllByOrganizationId(organizationId)
            .filter { it.role in roles }

        members.forEach { membership ->
            send(organizationId, membership.user.id, type, title, message, referenceType, referenceId, sendEmail)
        }
    }
}
