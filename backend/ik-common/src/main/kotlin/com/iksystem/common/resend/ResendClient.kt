package com.iksystem.common.resend

import com.iksystem.common.config.ResendConfig
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.http.*

@Component
class ResendClient(private val props: ResendConfig) {
    private val restTemplate = RestTemplate()

    fun sendEmail(to: String, subject: String, html: String) {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.setBearerAuth(props.apiKey)

        val body = mapOf(
            "from" to props.fromEmail,
            "to" to listOf(to),
            "subject" to subject,
            "html" to html
        )

        val request = HttpEntity(body, headers)

        restTemplate.exchange(
            "https://api.resend.com/emails",
            HttpMethod.POST,
            request,
            String::class.java
        )
    }
}