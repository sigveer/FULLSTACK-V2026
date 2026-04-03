package com.iksystem.common.documents.controller

import com.iksystem.common.documents.dto.DocumentUploadResponse
import com.iksystem.common.documents.dto.DocumentUrlResponse
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
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
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

    @Operation(summary = "Upload document", description = "Upload a document to S3 and store metadata. Returns presigned URL. Requires ADMIN or MANAGER role.")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Document uploaded successfully"),
        ApiResponse(responseCode = "400", description = "Invalid file"),
        ApiResponse(responseCode = "403", description = "Insufficient permissions"),
    )
    @PostMapping("/upload", consumes = ["multipart/form-data"])
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun uploadDocument(
        @RequestPart("file") file: MultipartFile,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<DocumentUploadResponse> {
        val document = documentsService.uploadFile(file, "documents", auth)
        val url = documentsService.getFileUrl(document.id, auth.requireOrganizationId())

        return ResponseEntity.status(HttpStatus.CREATED).body(
            DocumentUploadResponse(
                id = document.id,
                fileName = document.fileName,
                s3Key = document.s3Key,
                contentType = document.contentType,
                url = url
            )
        )
    }

    @Operation(summary = "Get document presigned URL", description = "Generate a fresh presigned URL for accessing a document. URL is valid for 1 hour.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Presigned URL generated successfully"),
        ApiResponse(responseCode = "403", description = "Insufficient permissions or document not found"),
        ApiResponse(responseCode = "404", description = "Document not found"),
    )
    @GetMapping("/{id}/url")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    fun getDocumentUrl(
        @Parameter(description = "Document ID") @PathVariable id: Long,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<DocumentUrlResponse> {
        val url = documentsService.getFileUrl(id, auth.requireOrganizationId())
        return ResponseEntity.ok(DocumentUrlResponse(id = id, url = url))
    }

    @Operation(summary = "Delete document", description = "Delete a document from S3 and database. Requires ADMIN or MANAGER role.")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Document deleted successfully"),
        ApiResponse(responseCode = "403", description = "Insufficient permissions or document not found"),
        ApiResponse(responseCode = "404", description = "Document not found"),
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    fun deleteDocument(
        @Parameter(description = "Document ID") @PathVariable id: Long,
        @AuthenticationPrincipal auth: AuthenticatedUser,
    ): ResponseEntity<Void> {
        documentsService.deleteFile(id, auth.requireOrganizationId())
        return ResponseEntity.noContent().build()
    }
}