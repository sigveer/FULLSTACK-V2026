package com.iksystem.common.notifications.repository

import com.iksystem.common.notifications.model.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

@Repository
interface NotificationsRepository : JpaRepository<Notification, Long> {
    fun findAllByRecipientUserIdAndOrganizationIdOrderByCreatedAtDesc(
        recipientUserId: Long,
        organizationId: Long,
        pageable: Pageable
    ): Page<Notification>

    fun countByRecipientUserIdAndOrganizationIdAndIsReadFalse(
        recipientUserId: Long,
        organizationId: Long
    ): Long

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.recipientUserId = :userId AND n.organizationId = :organizationId AND n.isRead = false")
    fun markAllAsRead(userId: Long, organizationId: Long)
}
