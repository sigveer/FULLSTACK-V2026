package com.iksystem.common.notifications.dto

import com.iksystem.common.notifications.model.NotificationType
import com.iksystem.common.notifications.model.ReferenceType

data class NotificationsResponse (
    val id: Long,
    val title: String,
    val message: String,
    val type: NotificationType,
    val referenceType: ReferenceType?,
    val referenceId: Long?,
    val isRead: Boolean,
    val createdAt: String
)

data class UnreadCountResponse(val count: Long)