package com.iksystem.`ik-common`.auth.controller

import com.iksystem.`ik-common`.auth.dto.AuthResponse
import com.iksystem.`ik-common`.auth.dto.LoginRequest
import com.iksystem.`ik-common`.auth.dto.LoginResponse
import com.iksystem.`ik-common`.auth.dto.RefreshRequest
import com.iksystem.`ik-common`.auth.dto.RegisterRequest
import com.iksystem.`ik-common`.auth.dto.SelectOrgRequest
import com.iksystem.`ik-common`.auth.service.AuthService
import com.iksystem.`ik-common`.membership.dto.MembershipSummary
import com.iksystem.`ik-common`.security.AuthenticatedUser
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller exposing two-phase authentication endpoints under `/api/v1/auth`.
 *
 * Flow: register/login (public) -> select-org (pre-auth JWT) -> full JWT issued.
 */
@Tag(name = "Authentication", description = "Two-phase auth: login, select organization, refresh, logout")
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
) {

    /**
     * Registers a new user identity. Returns a pre-auth token.
     * The user has no memberships yet — they must create or join an org.
     */
    @Operation(summary = "Register a new user", description = "Creates user identity. Returns pre-auth token and empty membership list.")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "User registered"),
        ApiResponse(responseCode = "400", description = "Validation error"),
        ApiResponse(responseCode = "409", description = "Email already registered"),
    )
    @PostMapping("/register")
    fun register(
        @Valid @RequestBody request: RegisterRequest,
    ): ResponseEntity<LoginResponse> {
        val response = authService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    /**
     * Phase 1: Login with email/password. Returns pre-auth token and list of memberships.
     */
    @Operation(summary = "Login", description = "Validates credentials. Returns pre-auth token and list of organizations the user belongs to.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Login successful — select an organization next"),
        ApiResponse(responseCode = "400", description = "Validation error"),
        ApiResponse(responseCode = "401", description = "Invalid credentials or account deactivated"),
    )
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest,
    ): ResponseEntity<LoginResponse> {
        val response = authService.login(request)
        return ResponseEntity.ok(response)
    }

    /**
     * Phase 2: Select an organization using the pre-auth token.
     * Returns a full org-scoped JWT + refresh token.
     */
    @Operation(summary = "Select organization", description = "Uses pre-auth token to select an org. Returns full JWT scoped to that org.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Organization selected, full tokens issued"),
        ApiResponse(responseCode = "403", description = "Not a member of the requested organization"),
        ApiResponse(responseCode = "404", description = "User not found"),
    )
    @PostMapping("/select-org")
    fun selectOrg(
        @Valid @RequestBody request: SelectOrgRequest,
        @AuthenticationPrincipal auth: AuthenticatedUser,
        httpRequest: HttpServletRequest,
    ): ResponseEntity<AuthResponse> {
        val response = authService.selectOrg(
            userId = auth.userId,
            request = request,
            ipAddress = httpRequest.remoteAddr,
            userAgent = httpRequest.getHeader("User-Agent"),
        )
        return ResponseEntity.ok(response)
    }

    /**
     * Refreshes an access token using a valid refresh token (token rotation).
     */
    @Operation(summary = "Refresh access token", description = "Rotates refresh token and issues a new org-scoped JWT.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Token refreshed"),
        ApiResponse(responseCode = "401", description = "Invalid, expired, or revoked refresh token"),
    )
    @PostMapping("/refresh")
    fun refresh(@Valid @RequestBody request: RefreshRequest): ResponseEntity<AuthResponse> {
        val response = authService.refresh(request)
        return ResponseEntity.ok(response)
    }

    /**
     * Lists all organizations the current user belongs to.
     * Useful for building an org-switcher in the dashboard.
     */
    @Operation(summary = "List memberships", description = "Returns all organizations the authenticated user is a member of.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Membership list returned"),
    )
    @GetMapping("/memberships")
    fun listMemberships(
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<List<MembershipSummary>> {
        val memberships = authService.listMemberships(auth.userId)
        return ResponseEntity.ok(memberships)
    }

    /**
     * Switches the user's active organization.
     * Revokes tokens for the current org and issues new ones scoped to the target org.
     */
    @Operation(summary = "Switch organization", description = "Switches active org context. Revokes current org tokens and issues new JWT scoped to target org.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Switched organization, new tokens issued"),
        ApiResponse(responseCode = "403", description = "Not a member of the requested organization"),
        ApiResponse(responseCode = "404", description = "User not found"),
    )
    @PostMapping("/switch-org")
    fun switchOrg(
        @Valid @RequestBody request: SelectOrgRequest,
        @AuthenticationPrincipal auth: AuthenticatedUser,
        httpRequest: HttpServletRequest,
    ): ResponseEntity<AuthResponse> {
        val response = authService.switchOrg(
            userId = auth.userId,
            currentOrgId = auth.requireOrganizationId(),
            request = request,
            ipAddress = httpRequest.remoteAddr,
            userAgent = httpRequest.getHeader("User-Agent"),
        )
        return ResponseEntity.ok(response)
    }

    /**
     * Logs out by revoking all refresh tokens and sessions.
     */
    @Operation(summary = "Logout", description = "Revokes all refresh tokens and deactivates all sessions.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Logged out"),
        ApiResponse(responseCode = "403", description = "Not authenticated"),
    )
    @PostMapping("/logout")
    fun logout(@AuthenticationPrincipal auth: AuthenticatedUser): ResponseEntity<Void> {
        authService.logout(auth.userId)
        return ResponseEntity.noContent().build()
    }
}
