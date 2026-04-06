package com.iksystem.common.training.controller

import com.iksystem.common.security.AuthenticatedUser
import com.iksystem.common.training.dto.CreateTrainingLogRequest
import com.iksystem.common.training.dto.TrainingLogResponse
import com.iksystem.common.training.dto.UpdateTrainingLogRequest
import com.iksystem.common.training.service.TrainingService
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

@Tag(name = "Training Logs", description = "Staff training record management")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/training-logs")
class TrainingController(
    private val trainingService: TrainingService,
) {

    @Operation(summary = "List training logs", description = "Returns all training logs for the active organization.")
    @ApiResponse(responseCode = "200", description = "Training log list returned")
    @GetMapping
    fun list(@AuthenticationPrincipal auth: AuthenticatedUser): ResponseEntity<List<TrainingLogResponse>> =
        ResponseEntity.ok(trainingService.list(auth))

    @Operation(summary = "Get training log", description = "Returns one training log by ID in the active organization.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Training log returned"),
        ApiResponse(responseCode = "404", description = "Training log not found"),
    )
    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: Long,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<TrainingLogResponse> =
        ResponseEntity.ok(trainingService.getById(id, auth))

    @Operation(summary = "Create training log", description = "Creates a new training log entry in the active organization.")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Training log created"),
        ApiResponse(responseCode = "400", description = "Validation error"),
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun create(
        @Valid @RequestBody request: CreateTrainingLogRequest,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<TrainingLogResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(trainingService.create(request, auth))

    @Operation(summary = "Update training log", description = "Updates a training log entry.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Training log updated"),
        ApiResponse(responseCode = "400", description = "No update fields or invalid payload"),
        ApiResponse(responseCode = "404", description = "Training log not found"),
    )
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateTrainingLogRequest,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<TrainingLogResponse> =
        ResponseEntity.ok(trainingService.update(id, request, auth))

    @Operation(summary = "Delete training log", description = "Deletes a training log entry.")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Training log deleted"),
        ApiResponse(responseCode = "404", description = "Training log not found"),
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun delete(
        @PathVariable id: Long,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<Void> {
        trainingService.delete(id, auth)
        return ResponseEntity.noContent().build()
    }
}
