package com.iksystem.common.deviation.controller

import com.iksystem.common.deviation.dto.CreateDeviationRequest
import com.iksystem.common.deviation.dto.DeviationResponse
import com.iksystem.common.deviation.dto.UpdateDeviationRequest
import com.iksystem.common.deviation.dto.UpdateDeviationStatusRequest
import com.iksystem.common.deviation.service.DeviationService
import com.iksystem.common.security.AuthenticatedUser
import io.swagger.v3.oas.annotations.Operation
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

@Tag(name = "Deviations", description = "Deviation management for IK-Mat and IK-Alkohol")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/deviations")
class DeviationController(
    private val deviationService: DeviationService,
) {

    @Operation(summary = "List deviations", description = "Returns all deviations for the active organization.")
    @ApiResponse(responseCode = "200", description = "Deviation list returned")
    @GetMapping
    fun list(@AuthenticationPrincipal auth: AuthenticatedUser): ResponseEntity<List<DeviationResponse>> =
        ResponseEntity.ok(deviationService.list(auth))

    @Operation(summary = "Get deviation", description = "Returns one deviation by ID in the active organization.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Deviation returned"),
        ApiResponse(responseCode = "404", description = "Deviation not found"),
    )
    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: Long,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<DeviationResponse> =
        ResponseEntity.ok(deviationService.getById(id, auth))

    @Operation(summary = "Create deviation", description = "Creates a new deviation in the active organization.")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Deviation created"),
        ApiResponse(responseCode = "400", description = "Validation error"),
    )
    @PostMapping
    fun create(
        @Valid @RequestBody request: CreateDeviationRequest,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<DeviationResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(deviationService.create(request, auth))

    @Operation(summary = "Update deviation", description = "Updates deviation content and assignment.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Deviation updated"),
        ApiResponse(responseCode = "400", description = "No update fields or invalid payload"),
        ApiResponse(responseCode = "404", description = "Deviation not found"),
    )
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateDeviationRequest,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<DeviationResponse> =
        ResponseEntity.ok(deviationService.update(id, request, auth))

    @Operation(summary = "Update status", description = "Updates only the status of a deviation.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Status updated"),
        ApiResponse(responseCode = "404", description = "Deviation not found"),
    )
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun updateStatus(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateDeviationStatusRequest,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<DeviationResponse> =
        ResponseEntity.ok(deviationService.updateStatus(id, request, auth))

    @Operation(summary = "Delete deviation", description = "Deletes a deviation.")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Deviation deleted"),
        ApiResponse(responseCode = "404", description = "Deviation not found"),
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun delete(
        @PathVariable id: Long,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<Void> {
        deviationService.delete(id, auth)
        return ResponseEntity.noContent().build()
    }
}
