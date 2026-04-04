package com.iksystem.common.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "resend")
class ResendConfig {
    lateinit var apiKey: String
    lateinit var fromEmail: String
    var devMode: Boolean = false
}