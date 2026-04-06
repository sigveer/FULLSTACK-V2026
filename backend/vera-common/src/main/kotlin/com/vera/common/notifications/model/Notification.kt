package com.iksystem.common.notifications.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

enum class NotificationType {
    BEVILLING_EXPIRY,
    TRAINING_EXPIRY,
    DEVIATION_CREATED,
    DEVIATION_ASSIGNED,
    CHECKLIST_OVERDUE,
    LOG_REMINDER,
    SYSTEM_ALERT
}

enum class ReferenceType {
    ALCOHOL_POLICY,
    TRAINING_LOG,
    DEVIATION,
    CHECKLIST,
    AGE_VERIFICATION_LOG
}

@Entity
@Table(name = "notifications")
data class Notification (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "organization_id", nullable = false)
    val organizationId: Long,

    @Column(name = "recipient_id", nullable = false)
    val recipientUserId: Long,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val message: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: NotificationType,

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type")
    val referenceType: ReferenceType? = null,

    @Column(name = "reference_id")
    val referenceId: Long? = null,

    @Column(name = "is_read", nullable = false)
    var isRead: Boolean = false,

    @Column(name = "read_at")
    var readAt: Instant? = null,

    @Column(name = "email_sent", nullable = false)
    var emailSent: Boolean = false,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now())