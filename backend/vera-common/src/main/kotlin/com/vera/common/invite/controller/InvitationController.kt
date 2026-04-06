package com.iksystem.common.invite.controller

import com.iksystem.common.auth.dto.AuthResponse
import com.iksystem.common.invite.dto.AcceptInviteRequest
import com.iksystem.common.invite.dto.InviteDetailsResponse
import com.iksystem.common.invite.dto.InviteUserRequest
import com.iksystem.common.invite.service.InvitationService
import com.iksystem.common.security.AuthenticatedUser
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Invitations", description = "Invite users to an organization")
@RestController
@RequestMapping("/api/v1/invitations")
class InvitationController(private val invitationService: InvitationService) {

    @Operation(summary = "Send invitation", description = "Sends an invite email to the given address. Requires ADMIN or MANAGER role.")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun invite(
        @Valid @RequestBody request: InviteUserRequest,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<Void> {
        invitationService.createInvite(request, auth)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @Operation(summary = "Get invitation details", description = "Returns public details for a pending invite (no auth required).")
    @GetMapping("/{token}")
    fun getInviteDetails(@PathVariable token: String): ResponseEntity<InviteDetailsResponse> {
        return ResponseEntity.ok(invitationService.getValidInvite(token))
    }

    @Operation(summary = "Accept invitation", description = "Accepts an invite and returns auth tokens (no auth required).")
    @PostMapping("/{token}/accept")
    fun accept(
        @PathVariable token: String,
        @Valid @RequestBody request: AcceptInviteRequest,
    ): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(
            invitationService.acceptInvitation(token, request.password, request.fullName, request.phoneNumber)
        )
    }
}
