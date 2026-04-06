package com.iksystem.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.http.HttpMethod
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import com.iksystem.common.security.JwtAuthFilter
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

/**
 * Spring Security configuration for the IK System backend.
 *
 * Configures stateless JWT-based authentication, CORS, and the HTTP
 * security filter chain. Auth endpoints (`/api/v1/auth/`) are publicly
 * accessible; all other routes require a valid JWT.
 *
 * @property jwtAuthFilter Filter that extracts and validates JWTs from incoming requests.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(private val jwtAuthFilter: JwtAuthFilter) {

    /**
     * Builds the main security filter chain.
     *
     * Disables CSRF (stateless API), enforces CORS, adds the [jwtAuthFilter]
     * before Spring's default username/password filter, and permits auth
     * endpoints without authentication.
     */
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/auth/refresh").permitAll()
                    .requestMatchers("/api/v1/invitations/{token}", "/api/v1/invitations/{token}/accept").permitAll()
                    .requestMatchers("/api/v1/auth/select-org", "/api/v1/auth/logout").authenticated()
                    .requestMatchers("/error").permitAll()
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                    .requestMatchers("/h2-console/**").permitAll()
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .anyRequest().authenticated()
            }
            .headers { it.frameOptions { fo -> fo.sameOrigin() } }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()

    /** Provides a BCrypt password encoder with strength 12. */
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder(12)

    /**
     * Configures CORS to allow requests from local dev servers
     * (`localhost:5173`, `localhost:3000` and `localhost:80`).
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration().apply {
            allowedOrigins = listOf("http://localhost:8081","http://localhost:5173", "http://localhost:3000", "http://localhost:80", "http://localhost", "http://localhost:8080")
            allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            allowedHeaders = listOf("Authorization", "Content-Type")
            allowCredentials = true
            maxAge = 3600L
        }
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/api/**", config)
        }
    }
}