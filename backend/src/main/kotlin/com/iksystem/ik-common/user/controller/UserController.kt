package com.iksystem.`ik-common`.user.controller

import com.iksystem.`ik-common`.security.AuthenticatedUser
import com.iksystem.`ik-common`.user.dto.CreateUserRequest
import com.iksystem.`ik-common`.user.dto.UpdateUserRoleRequest
import com.iksystem.`ik-common`.user.dto.UserResponse
import com.iksystem.`ik-common`.user.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller exposing user-management endpoints under `/api/v1/users`.
 *
 * Most endpoints require the `ADMIN` or `MANAGER` role. All operations are
 * scoped to the caller's organization via [AuthenticatedUser].
 *
 * @property userService Service that handles user business logic.
 */
@RestController
@RequestMapping("/api/v1/users")
class UserController(private val userService: UserService) {

    /**
     * Lists all users in the caller's organization.
     *
     * @param auth The authenticated caller (injected by Spring Security).
     * @return `200 OK` with a list of [UserResponse] DTOs.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun listUsers(@AuthenticationPrincipal auth: AuthenticatedUser): ResponseEntity<List<UserResponse>> =
        ResponseEntity.ok(userService.listUsers(auth))

    /**
     * Retrieves a single user by ID.
     *
     * @param id Path variable — the user's primary key.
     * @param auth The authenticated caller.
     * @return `200 OK` with the [UserResponse] body.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun getUser(
        @PathVariable id: Long,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.getUser(id, auth))

    /**
     * Returns the profile of the currently authenticated user.
     *
     * This endpoint is available to any authenticated user regardless of role.
     *
     * @param auth The authenticated caller.
     * @return `200 OK` with the caller's [UserResponse].
     */
    @GetMapping("/me")
    fun getCurrentUser(@AuthenticationPrincipal auth: AuthenticatedUser): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.getUser(auth.userId, auth))

    /**
     * Creates a new user in the caller's organization.
     *
     * @param request Validated [CreateUserRequest] payload.
     * @param auth The authenticated caller.
     * @return `201 Created` with the [UserResponse] body.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun createUser(
        @Valid @RequestBody request: CreateUserRequest,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<UserResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request, auth))

    /**
     * Updates the role of an existing user.
     *
     * @param id Path variable — the target user's primary key.
     * @param request Validated [UpdateUserRoleRequest] containing the new role.
     * @param auth The authenticated caller.
     * @return `200 OK` with the updated [UserResponse].
     */
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun updateUserRole(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateUserRoleRequest,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.updateUserRole(id, request, auth))

    /**
     * Deactivates a user account, preventing them from logging in.
     *
     * @param id Path variable — the target user's primary key.
     * @param auth The authenticated caller.
     * @return `200 OK` with the deactivated [UserResponse].
     */
    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun deactivateUser(
        @PathVariable id: Long,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.deactivateUser(id, auth))

    /**
     * Re-activates a previously deactivated user account.
     *
     * @param id Path variable — the target user's primary key.
     * @param auth The authenticated caller.
     * @return `200 OK` with the re-activated [UserResponse].
     */
    @PostMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun activateUser(
        @PathVariable id: Long,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.activateUser(id, auth))

    /**
     * Kicks a user by revoking their sessions and refresh tokens.
     *
     * The user account remains active but must re-authenticate.
     *
     * @param id Path variable — the target user's primary key.
     * @param auth The authenticated caller.
     * @return `204 No Content` on success.
     */
    @PostMapping("/{id}/kick")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun kickUser(
        @PathVariable id: Long,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<Void> {
        userService.kickUser(id, auth)
        return ResponseEntity.noContent().build()
    }
}