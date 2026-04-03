package com.iksystem.common.documents.controller

import com.iksystem.common.documents.dto.DocumentsDtos
import com.iksystem.common.documents.service.DocumentsService
import com.iksystem.common.security.AuthenticatedUser
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/documents")
@Tag(name = "Documents", description = "Document management and upload to S3")
@SecurityRequirement(name = "bearerAuth")
class DocumentsController(private val documentsService: DocumentsService) {

    @GetMapping("/test-s3")
    fun test(): String {
        documentsService.test()
        return "OK"
    }

    @Operation(summary = "Upload document", description = "Upload a document to S3. Requires ADMIN or MANAGER role.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Document uploaded successfully"),
        ApiResponse(responseCode = "400", description = "Invalid file"),
        ApiResponse(responseCode = "403", description = "Insufficient permissions"),
    )
    @PostMapping("/upload", consumes = ["multipart/form-data"])
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun uploadDocument(
        @RequestPart("file") file: MultipartFile,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<DocumentsDtos> {
        val key = documentsService.uploadFile(file, "documents")
        val url = documentsService.getFileUrl(key)

        return ResponseEntity.status(HttpStatus.OK).body(
            DocumentsDtos(
                fileName = key,
                url = url
            )
        )
    }

    @Operation(summary = "Delete document", description = "Delete a document from S3. Requires ADMIN or MANAGER role.")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Document deleted successfully"),
        ApiResponse(responseCode = "403", description = "Insufficient permissions"),
    )
    @DeleteMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun deleteDocument(
        @Parameter(description = "S3 object key") @RequestParam key: String,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<Void> {
        documentsService.deleteFile(key)
        return ResponseEntity.noContent().build()
    }
}