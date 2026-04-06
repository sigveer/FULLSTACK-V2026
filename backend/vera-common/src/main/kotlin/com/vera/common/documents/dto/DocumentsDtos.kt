package com.iksystem.common.documents.dto

data class DocumentUploadResponse(
    val id: Long,
    val fileName: String,
    val s3Key: String,
    val contentType: String,
    val url: String, // Presigned URL (temporary)
)

data class DocumentUrlResponse(
    val id: Long,
    val url: String, // Presigned URL (temporary)
)