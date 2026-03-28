package com.iksystem.`ik-common`.organization.controller

import com.iksystem.`ik-common`.organization.dto.CreateOrganizationRequest
import com.iksystem.`ik-common`.organization.dto.OrganizationResponse
import com.iksystem.`ik-common`.organization.service.OrganizationService
import com.iksystem.`ik-common`.security.AuthenticatedUser
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller for organization management.
 * Creating an org requires any authenticated user (even pre-auth).
 * The creator automatically becomes ADMIN of the new org.
 */
@Tag(name = "Organizations", description = "Organization management")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/organizations")
class OrganizationController(private val organizationService: OrganizationService) {

    /**
     * Creates a new organization. The caller becomes its ADMIN.
     */
    @Operation(summary = "Create organization", description = "Creates a new org. The caller automatically becomes ADMIN.")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Organization created"),
        ApiResponse(responseCode = "400", description = "Validation error"),
        ApiResponse(responseCode = "409", description = "Organization name already exists"),
    )
    @PostMapping
    fun create(
        @Valid @RequestBody request: CreateOrganizationRequest,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<OrganizationResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(organizationService.create(request, auth.userId))

    /**
     * Retrieves a single organization by its ID.
     *
     * @param id The organization's primary key.
     * @return The matching [OrganizationResponse].
     */
    @Operation(summary = "Get organization by ID", description = "Returns a single organization.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Organization found"),
        ApiResponse(responseCode = "404", description = "Organization not found"),
    )
    @GetMapping("/{id}")
    fun getById(@Parameter(description = "Organization ID") @PathVariable id: Long): ResponseEntity<OrganizationResponse> =
        ResponseEntity.ok(organizationService.getById(id))

    /**
     * Lists every organization in the system.
     *
     * @return A list of all [OrganizationResponse] entries.
     */
    @Operation(summary = "List all organizations", description = "Returns all organizations in the system.")
    @ApiResponse(responseCode = "200", description = "Organization list returned")
    @GetMapping
    fun listAll(): ResponseEntity<List<OrganizationResponse>> =
        ResponseEntity.ok(organizationService.listAll())

    /**
     * Deletes an organization and all associated memberships.
     *
     * @param id The organization's primary key.
     */
    @Operation(summary = "Delete organization", description = "Deletes an organization and all its memberships.")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Organization deleted"),
        ApiResponse(responseCode = "404", description = "Organization not found"),
    )
    @DeleteMapping("/{id}")
    fun delete(@Parameter(description = "Organization ID") @PathVariable id: Long): ResponseEntity<Void> {
        organizationService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
