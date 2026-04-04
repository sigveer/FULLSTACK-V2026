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
            title = "Welcom to Ik-system",
            description = "Hi, ${fullName} \n Thank you for joining!",
        )
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