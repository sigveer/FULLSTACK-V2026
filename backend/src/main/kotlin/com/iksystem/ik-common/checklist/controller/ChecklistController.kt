package com.iksystem.`ik-common`.checklist.controller

import com.iksystem.`ik-common`.checklist.dto.ChecklistItemResponse
import com.iksystem.`ik-common`.checklist.dto.ChecklistResponse
import com.iksystem.`ik-common`.checklist.dto.ChecklistStatsResponse
import com.iksystem.`ik-common`.checklist.dto.CreateChecklistItemRequest
import com.iksystem.`ik-common`.checklist.dto.CreateChecklistRequest
import com.iksystem.`ik-common`.checklist.dto.SetChecklistCompletionRequest
import com.iksystem.`ik-common`.checklist.dto.UpdateChecklistItemRequest
import com.iksystem.`ik-common`.checklist.dto.UpdateChecklistRequest
import com.iksystem.`ik-common`.checklist.service.ChecklistService
import com.iksystem.`ik-common`.security.AuthenticatedUser
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

@Tag(name = "Checklists", description = "Checklist templates and checklist items")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/checklists")
class ChecklistController(
    private val checklistService: ChecklistService,
) {

    @Operation(summary = "List checklists", description = "Returns all checklists for the active organization.")
    @ApiResponse(responseCode = "200", description = "Checklist list returned")
    @GetMapping
    fun list(@AuthenticationPrincipal auth: AuthenticatedUser): ResponseEntity<List<ChecklistResponse>> =
        ResponseEntity.ok(checklistService.list(auth))

    @Operation(summary = "Checklist stats", description = "Returns summary stats for dashboard counters.")
    @ApiResponse(responseCode = "200", description = "Checklist stats returned")
    @GetMapping("/stats")
    fun stats(@AuthenticationPrincipal auth: AuthenticatedUser): ResponseEntity<ChecklistStatsResponse> =
        ResponseEntity.ok(checklistService.stats(auth))

    @Operation(summary = "Create checklist", description = "Creates a new checklist template.")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Checklist created"),
        ApiResponse(responseCode = "400", description = "Validation error"),
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun createChecklist(
        @Valid @RequestBody request: CreateChecklistRequest,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<ChecklistResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(checklistService.createChecklist(request, auth))

    @Operation(summary = "Update checklist", description = "Updates checklist metadata and status.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Checklist updated"),
        ApiResponse(responseCode = "400", description = "No update fields or validation error"),
        ApiResponse(responseCode = "404", description = "Checklist not found"),
    )
    @PatchMapping("/{checklistId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun updateChecklist(
        @PathVariable checklistId: Long,
        @Valid @RequestBody request: UpdateChecklistRequest,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<ChecklistResponse> =
        ResponseEntity.ok(checklistService.updateChecklist(checklistId, request, auth))

    @Operation(summary = "Delete checklist", description = "Deletes a checklist and all of its items.")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Checklist deleted"),
        ApiResponse(responseCode = "404", description = "Checklist not found"),
    )
    @DeleteMapping("/{checklistId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun deleteChecklist(
        @PathVariable checklistId: Long,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<Void> {
        checklistService.deleteChecklist(checklistId, auth)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "Set checklist completion", description = "Marks all items in a checklist as completed or not completed.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Checklist completion updated"),
        ApiResponse(responseCode = "404", description = "Checklist not found"),
    )
    @PatchMapping("/{checklistId}/completion")
    fun setChecklistCompletion(
        @PathVariable checklistId: Long,
        @Valid @RequestBody request: SetChecklistCompletionRequest,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<ChecklistResponse> =
        ResponseEntity.ok(checklistService.setChecklistCompletion(checklistId, request, auth))

    @Operation(summary = "Create checklist item", description = "Adds an item to a checklist.")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Checklist item created"),
        ApiResponse(responseCode = "404", description = "Checklist not found"),
    )
    @PostMapping("/{checklistId}/items")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun createItem(
        @PathVariable checklistId: Long,
        @Valid @RequestBody request: CreateChecklistItemRequest,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<ChecklistItemResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(checklistService.createItem(checklistId, request, auth))

    @Operation(summary = "Update checklist item", description = "Updates a checklist item.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Checklist item updated"),
        ApiResponse(responseCode = "404", description = "Checklist or checklist item not found"),
    )
    @PatchMapping("/{checklistId}/items/{itemId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun updateItem(
        @PathVariable checklistId: Long,
        @PathVariable itemId: Long,
        @Valid @RequestBody request: UpdateChecklistItemRequest,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<ChecklistItemResponse> =
        ResponseEntity.ok(checklistService.updateItem(checklistId, itemId, request, auth))

    @Operation(summary = "Delete checklist item", description = "Removes an item from a checklist.")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Checklist item deleted"),
        ApiResponse(responseCode = "404", description = "Checklist or checklist item not found"),
    )
    @DeleteMapping("/{checklistId}/items/{itemId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun deleteItem(
        @PathVariable checklistId: Long,
        @PathVariable itemId: Long,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<Void> {
        checklistService.deleteItem(checklistId, itemId, auth)
        return ResponseEntity.noContent().build()
    }
}
