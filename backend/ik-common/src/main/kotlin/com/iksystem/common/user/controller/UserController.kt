package com.iksystem.common.user.controller

import com.iksystem.common.membership.dto.MembershipResponse
import com.iksystem.common.security.AuthenticatedUser
import com.iksystem.common.user.dto.CreateUserRequest
import com.iksystem.common.user.dto.UpdateUserRoleRequest
import com.iksystem.common.user.dto.UserResponse
import com.iksystem.common.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller for user management within the caller's organization.
 * Returns membership-scoped data (user + role in this org).
 */
@Tag(name = "Users", description = "User and membership management within an organization")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/users")
class UserController(private val userService: UserService) {

    /**
     * Lists all members in the caller's organization.
     *
     * @param auth The authenticated user, used to determine the current organization.
     * @return A list of [MembershipResponse] for every member in the org.
     */
    @Operation(summary = "List members", description = "Returns all members in the caller's organization. Requires ADMIN or MANAGER.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Member list returned"),
        ApiResponse(responseCode = "403", description = "Insufficient permissions"),
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun listUsers(@AuthenticationPrincipal auth: AuthenticatedUser): ResponseEntity<List<MembershipResponse>> =
        ResponseEntity.ok(userService.listUsers(auth))

    /**
     * Retrieves a single member's details within the caller's organization.
     *
     * @param id The target user's primary key.
     * @param auth The authenticated user, used to determine the current organization.
     * @return The matching [MembershipResponse].
     */
    @Operation(summary = "Get member by user ID", description = "Returns a user's membership in the caller's org. Requires ADMIN or MANAGER.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Member found"),
        ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        ApiResponse(responseCode = "404", description = "User not found in this organization"),
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun getUser(
        @Parameter(description = "User ID") @PathVariable id: Long,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<MembershipResponse> =
        ResponseEntity.ok(userService.getUser(id, auth))

    /**
     * Returns the currently authenticated user's own profile.
     *
     * @param auth The authenticated user principal.
     * @return The caller's [UserResponse].
     */
    @Operation(summary = "Get current user profile", description = "Returns the authenticated user's identity. Any role allowed.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Current user profile returned"),
        ApiResponse(responseCode = "403", description = "Not authenticated"),
    )
    @GetMapping("/me")
    fun getCurrentUser(@AuthenticationPrincipal auth: AuthenticatedUser): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.getCurrentUser(auth))

    /**
     * Creates a new user and adds them to the caller's organization,
     * or adds an existing user if the email already exists.
     *
     * @param request The user creation payload (email, password, name, phone, role).
     * @param auth The authenticated user, used to determine the current organization.
     * @return The newly created [MembershipResponse].
     */
    @Operation(summary = "Create user and add to org", description = "Creates a user (or adds existing) to the caller's org. Requires ADMIN or MANAGER.")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "User created/added to organization"),
        ApiResponse(responseCode = "400", description = "Validation error or invalid role"),
        ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        ApiResponse(responseCode = "409", description = "User already a member of this organization"),
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun createUser(
        @Valid @RequestBody request: CreateUserRequest,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<MembershipResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request, auth))

    /**
     * Updates a member's role within the caller's organization.
     *
     * @param id The target user's primary key.
     * @param request The new role to assign.
     * @param auth The authenticated user, used to determine the current organization.
     * @return The updated [MembershipResponse].
     */
    @Operation(summary = "Update member role", description = "Changes a member's role in the caller's org. Requires ADMIN or MANAGER.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Role updated"),
        ApiResponse(responseCode = "400", description = "Invalid role"),
        ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        ApiResponse(responseCode = "404", description = "User not found in this organization"),
    )
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun updateUserRole(
        @Parameter(description = "User ID") @PathVariable id: Long,
        @Valid @RequestBody request: UpdateUserRoleRequest,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<MembershipResponse> =
        ResponseEntity.ok(userService.updateUserRole(id, request, auth))

    /**
     * Globally deactivates a user account. The user will not be able to log in
     * until re-activated. Cannot deactivate yourself.
     *
     * @param id The target user's primary key.
     * @param auth The authenticated user, used for authorization and self-check.
     * @return The updated [UserResponse] with `active = false`.
     */
    @Operation(summary = "Deactivate user", description = "Disables a user account globally. Cannot deactivate yourself. Requires ADMIN or MANAGER.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "User deactivated"),
        ApiResponse(responseCode = "403", description = "Insufficient permissions or self-deactivation"),
        ApiResponse(responseCode = "404", description = "User not found in this organization"),
    )
    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun deactivateUser(
        @Parameter(description = "User ID") @PathVariable id: Long,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.deactivateUser(id, auth))

    /**
     * Re-enables a previously deactivated user account.
     *
     * @param id The target user's primary key.
     * @param auth The authenticated user, used for authorization.
     * @return The updated [UserResponse] with `active = true`.
     */
    @Operation(summary = "Activate user", description = "Re-enables a deactivated user. Requires ADMIN or MANAGER.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "User activated"),
        ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        ApiResponse(responseCode = "404", description = "User not found in this organization"),
    )
    @PostMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun activateUser(
        @Parameter(description = "User ID") @PathVariable id: Long,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.activateUser(id, auth))

    /**
     * Kicks a user by revoking all their sessions and refresh tokens,
     * forcing them to re-authenticate. Cannot kick yourself.
     *
     * @param id The target user's primary key.
     * @param auth The authenticated user, used for authorization and self-check.
     */
    @Operation(summary = "Kick user", description = "Revokes sessions and tokens — user must re-login. Cannot kick yourself. Requires ADMIN or MANAGER.")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "User kicked"),
        ApiResponse(responseCode = "403", description = "Insufficient permissions or self-kick"),
        ApiResponse(responseCode = "404", description = "User not found in this organization"),
    )
    @PostMapping("/{id}/kick")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun kickUser(
        @Parameter(description = "User ID") @PathVariable id: Long,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<Void> {
        userService.kickUser(id, auth)
        return ResponseEntity.noContent().build()
    }

    /**
     * Removes a member from the caller's organization. Their user account
     * remains in the system but they lose access to this org. Cannot remove yourself.
     *
     * @param id The target user's primary key.
     * @param auth The authenticated user, used for authorization and self-check.
     */
    @Operation(summary = "Remove member", description = "Removes a user from the caller's organization. Cannot remove yourself. Requires ADMIN.")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Member removed"),
        ApiResponse(responseCode = "403", description = "Insufficient permissions or self-removal"),
        ApiResponse(responseCode = "404", description = "User not found in this organization"),
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun removeMember(
        @Parameter(description = "User ID") @PathVariable id: Long,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<Void> {
        userService.removeMember(id, auth)
        return ResponseEntity.noContent().build()
    }
}
