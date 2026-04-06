package com.iksystem.common.notifications.schedulers

import com.iksystem.common.checklist.model.ChecklistFrequency
import com.iksystem.common.checklist.repository.ChecklistItemRepository
import com.iksystem.common.checklist.repository.ChecklistRepository
import com.iksystem.common.notifications.model.NotificationType
import com.iksystem.common.notifications.model.ReferenceType
import com.iksystem.common.notifications.service.NotificationsService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ChecklistScheduler(
    private val checklistRepository: ChecklistRepository,
    private val checklistItemRepository: ChecklistItemRepository,
    private val notificationsService: NotificationsService,
) {
    private val log = LoggerFactory.getLogger(ChecklistScheduler::class.java)

    // Daily checklists — check every day at 22:00 (end of business day)
    @Scheduled(cron = "0 0 22 * * *")
    fun checkDailyChecklists() {
        log.info("Running daily checklist overdue check")
        notifyIncompleteChecklists(ChecklistFrequency.DAILY)
    }

    // Weekly checklists — check every Sunday at 22:00
    @Scheduled(cron = "0 0 22 * * SUN")
    fun checkWeeklyChecklists() {
        log.info("Running weekly checklist overdue check")
        notifyIncompleteChecklists(ChecklistFrequency.WEEKLY)
    }

    // Monthly checklists — check last day of month at 22:00
    @Scheduled(cron = "0 0 22 L * *")
    fun checkMonthlyChecklists() {
        log.info("Running monthly checklist overdue check")
        notifyIncompleteChecklists(ChecklistFrequency.MONTHLY)
    }

    // Yearly checklists — check December 31st at 22:00
    @Scheduled(cron = "0 0 22 31 12 *")
    fun checkYearlyChecklists() {
        log.info("Running yearly checklist overdue check")
        notifyIncompleteChecklists(ChecklistFrequency.YEARLY)
    }

    private fun notifyIncompleteChecklists(frequency: ChecklistFrequency) {
        val incompleteChecklists = checklistRepository.findIncompleteByFrequency(frequency)

        if (incompleteChecklists.isEmpty()) {
            log.info("No incomplete {} checklists found", frequency)
            return
        }

        log.info("Found {} incomplete {} checklists", incompleteChecklists.size, frequency)

        incompleteChecklists.forEach { checklist ->
            val incompleteCount = checklistItemRepository.countByChecklistIdAndCompletedFalse(checklist.id)
            val message = "Checklist '${checklist.name}' ($frequency) has $incompleteCount incomplete item(s) and is overdue."

            notificationsService.sendToOrgAdminsAndManagers(
                organizationId = checklist.organizationId,
                type = NotificationType.CHECKLIST_OVERDUE,
                title = "Checklist Overdue: ${checklist.name}",
                message = message,
                referenceType = ReferenceType.CHECKLIST,
                referenceId = checklist.id
            )
        }
    }
}
