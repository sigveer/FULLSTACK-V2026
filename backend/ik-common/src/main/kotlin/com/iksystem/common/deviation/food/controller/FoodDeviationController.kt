package com.iksystem.common.deviation.food.controller

import com.iksystem.common.deviation.food.dto.CreateFoodDeviationRequest
import com.iksystem.common.deviation.food.dto.FoodDeviationResponse
import com.iksystem.common.deviation.food.dto.UpdateFoodDeviationRequest
import com.iksystem.common.deviation.food.service.FoodDeviationService
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

@Tag(name = "Food Deviations", description = "Deviation management for IK-Mat")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/deviations/food")
class FoodDeviationController(
    private val service: FoodDeviationService,
) {

    @Operation(summary = "List food deviations")
    @ApiResponse(responseCode = "200", description = "Food deviation list returned")
    @GetMapping
    fun list(@AuthenticationPrincipal auth: AuthenticatedUser): ResponseEntity<List<FoodDeviationResponse>> =
        ResponseEntity.ok(service.list(auth))

    @Operation(summary = "Get food deviation by ID")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Food deviation returned"),
        ApiResponse(responseCode = "404", description = "Not found"),
    )
    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: Long,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<FoodDeviationResponse> =
        ResponseEntity.ok(service.getById(id, auth))

    @Operation(summary = "Create food deviation")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Food deviation created"),
        ApiResponse(responseCode = "400", description = "Validation error"),
    )
    @PostMapping
    fun create(
        @Valid @RequestBody request: CreateFoodDeviationRequest,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<FoodDeviationResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, auth))

    @Operation(summary = "Update food deviation")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Food deviation updated"),
        ApiResponse(responseCode = "404", description = "Not found"),
    )
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateFoodDeviationRequest,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<FoodDeviationResponse> =
        ResponseEntity.ok(service.update(id, request, auth))

    @Operation(summary = "Delete food deviation")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Food deviation deleted"),
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
