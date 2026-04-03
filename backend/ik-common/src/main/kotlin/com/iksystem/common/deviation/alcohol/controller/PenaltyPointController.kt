package com.iksystem.common.deviation.alcohol.controller

import com.iksystem.common.deviation.alcohol.dto.CreatePenaltyPointRequest
import com.iksystem.common.deviation.alcohol.dto.PenaltyPointResponse
import com.iksystem.common.deviation.alcohol.dto.PenaltyPointSummaryResponse
import com.iksystem.common.deviation.alcohol.service.PenaltyPointService
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

@Tag(name = "Penalty Points", description = "Alcohol deviation penalty point management")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/penalty-points")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'MANAGER')")
class PenaltyPointController(
    private val service: PenaltyPointService,
) {

    @Operation(summary = "Get penalty points summary", description = "Returns total points and all entries for the organization")
    @ApiResponse(responseCode = "200", description = "Summary returned")
    @GetMapping
    fun getSummary(@AuthenticationPrincipal auth: AuthenticatedUser): ResponseEntity<PenaltyPointSummaryResponse> =
        ResponseEntity.ok(service.getSummary(auth))

    @Operation(summary = "Add penalty points manually")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Penalty point entry created"),
        ApiResponse(responseCode = "400", description = "Validation error"),
    )
    @PostMapping
    fun add(
        @Valid @RequestBody request: CreatePenaltyPointRequest,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<PenaltyPointResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.add(request, auth))

    @Operation(summary = "Delete penalty point entry")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Penalty point entry deleted"),
        ApiResponse(responseCode = "404", description = "Not found"),
    )
    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<Void> {
        service.delete(id, auth)
        return ResponseEntity.noContent().build()
    }
}
