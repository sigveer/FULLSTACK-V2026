package com.iksystem.alcohol.deviation.controller

import com.iksystem.alcohol.deviation.dto.AlcoholDeviationResponse
import com.iksystem.alcohol.deviation.dto.CreateAlcoholDeviationRequest
import com.iksystem.alcohol.deviation.dto.UpdateAlcoholDeviationRequest
import com.iksystem.alcohol.deviation.service.AlcoholDeviationService
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
import org.springframework.web.bind.annotation.*

@Tag(name = "Alcohol Deviations", description = "Deviation management for IK-Alkohol")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/deviations/alcohol")
class AlcoholDeviationController(
    private val service: AlcoholDeviationService,
) {

    @Operation(summary = "List alcohol deviations")
    @ApiResponse(responseCode = "200", description = "Alcohol deviation list returned")
    @GetMapping
    fun list(@AuthenticationPrincipal auth: AuthenticatedUser): ResponseEntity<List<AlcoholDeviationResponse>> =
        ResponseEntity.ok(service.list(auth))

    @Operation(summary = "Get alcohol deviation by ID")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Alcohol deviation returned"),
        ApiResponse(responseCode = "404", description = "Not found"),
    )
    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: Long,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<AlcoholDeviationResponse> =
        ResponseEntity.ok(service.getById(id, auth))

    @Operation(summary = "Create alcohol deviation")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Alcohol deviation created"),
        ApiResponse(responseCode = "400", description = "Validation error"),
    )
    @PostMapping
    fun create(
        @Valid @RequestBody request: CreateAlcoholDeviationRequest,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<AlcoholDeviationResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, auth))

    @Operation(summary = "Update alcohol deviation")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Alcohol deviation updated"),
        ApiResponse(responseCode = "404", description = "Not found"),
    )
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateAlcoholDeviationRequest,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<AlcoholDeviationResponse> =
        ResponseEntity.ok(service.update(id, request, auth))

    @Operation(summary = "Delete alcohol deviation")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Alcohol deviation deleted"),
        ApiResponse(responseCode = "404", description = "Not found"),
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun delete(
        @PathVariable id: Long,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<Void> {
        service.delete(id, auth)
        return ResponseEntity.noContent().build()
    }
}
