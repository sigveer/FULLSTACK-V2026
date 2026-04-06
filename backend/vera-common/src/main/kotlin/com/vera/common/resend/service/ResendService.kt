package com.iksystem.common.resend.service

import com.iksystem.common.config.ResendConfig
import com.iksystem.common.resend.EmailTemplateBuilder
import com.iksystem.common.resend.ResendClient
import org.springframework.stereotype.Service

@Service
class ResendService(
    private val resendClient: ResendClient,
    private val tempBuilder: EmailTemplateBuilder,
    private val props: ResendConfig,
) {
    fun sendRegistrationEmail(email: String, fullName: String) {
        val html = tempBuilder.registrationEmail(
            title = "Welcome to IK-system",
            description = "Hi, ${fullName}\nThank you for joining!",
        )
        resendClient.sendEmail(email, "Welcome to IK-system", html)
    }

    fun sendVerificationEmail(email: String, token: String) {
        val url = "test/verify?token=$token"

        val html = tempBuilder.actionEmail(
            title = "Verify your account",
            description = "Click below to verify your account.",
            buttonText = "Verify Account",
            buttonUrl = url
        )

        resendClient.sendEmail(email, "Verify your account", html)
    }

    fun sendNotificationEmail(email: String, title: String, message: String) {
        val html = tempBuilder.actionEmail(
            title = title,
            description = message,
            buttonText = "View in App",
            buttonUrl = "https://your-app-url.com"
        )
        resendClient.sendEmail(email, title, html)
    }

    fun sendInviteEmail(email: String, orgName: String, token: String) {
        val inviteUrl = "http://localhost:80/invite?token=$token"

        val html = tempBuilder.actionEmail(
            title = "Du er invitert til $orgName",
            description = "Du har blitt invitert til å bli med i IK-systemet for $orgName. Klikk på knappen under for å fullføre din profil.",
            buttonText = "Godta invitasjon",
            buttonUrl = inviteUrl
        )
        resendClient.sendEmail(email, "Invitasjon til $orgName", html)
    }

    fun sendChecklistAlert(
        email: String,
        checklistName: String,
        dueDate: String
    ) {

        val html = tempBuilder.statusCard(
            title = checklistName,
            rows = listOf(
                "Due date" to dueDate,
                "Status" to "Expiring soon"
            ),
            statusLabel = "Expires Soon",
            statusColor = "#B7791F"
        )

        resendClient.sendEmail(email, "Checklist expiring soon", html)
    }

    fun sendTrainingExpiringSoon(
        email: String,
        employee: String,
    ) {
        val html = tempBuilder.statusCard(
            title = "Missing Certification",
            rows = listOf(
                "Employee" to employee,
                "Status" to "Missing"
            ),
            statusLabel = "Expiring soon",
            statusColor = "#B7791F"
        )

        resendClient.sendEmail(email, "Training Missing", html)
    }

    fun sendDeviationEmail(
        email: String,
        deviationType: String,
        severity: String,
        reportedBy: String,
        description: String
    ) {
        val html = tempBuilder.statusCard(
            title = "New Deviation Reported",
            rows = listOf(
                "Type" to deviationType,
                "Severity" to severity,
                "Reported by" to reportedBy,
                "Description" to description.take(100)
            ),
            statusLabel = "Action Required",
            statusColor = "#C53030"
        )
        resendClient.sendEmail(email, "New Deviation: $deviationType", html)
    }

    fun sendChecklistOverdueEmail(
        email: String,
        checklistName: String,
        frequency: String,
        incompleteItems: Int
    ) {
        val html = tempBuilder.statusCard(
            title = "Checklist Overdue",
            rows = listOf(
                "Checklist" to checklistName,
                "Frequency" to frequency,
                "Incomplete items" to incompleteItems.toString()
            ),
            statusLabel = "Overdue",
            statusColor = "#C53030"
        )
        resendClient.sendEmail(email, "Checklist Overdue: $checklistName", html)
    }

    fun sendTrainingMissing(
        email: String,
        employee: String
    ) {
        val html = tempBuilder.statusCard(
            title = "Missing Certification",
            rows = listOf(
                "Employee" to employee,
                "Status" to "Missing"
            ),
            statusLabel = "Action Required",
            statusColor = "#C53030"
        )

        resendClient.sendEmail(email, "Training Missing", html)
    }
}