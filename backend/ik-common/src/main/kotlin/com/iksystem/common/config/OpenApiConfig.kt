package com.iksystem.common.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.context.annotation.Configuration

/**
 * OpenAPI/Swagger configuration.
 *
 * Defines the API metadata and the JWT Bearer security scheme
 * referenced by `@SecurityRequirement(name = "bearerAuth")` on controllers.
 */
@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "IK System API",
        version = "1.0",
        description = "Backend API for the IK System — authentication, user management, and organization management."
    )
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "JWT access token obtained from /api/v1/auth/login"
)
class OpenApiConfig
