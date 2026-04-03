package com.iksystem.common.documents.service

import com.iksystem.common.documents.model.Document
import com.iksystem.common.documents.repository.DocumentRepository
import com.iksystem.common.security.AuthenticatedUser
import com.iksystem.common.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.time.Duration
import java.util.UUID

@Service
class DocumentsService(
    private val s3Client: S3Client,
    private val s3Presigner: S3Presigner,
    private val documentRepository: DocumentRepository,
    private val userRepository: UserRepository,
    @Value("\${aws.s3.bucket}") val bucketName: String,
) {
    fun test() {
        s3Client.listBuckets()
    }

    /**
     * Uploads a file to S3 and stores document metadata in the database.
     *
     * @param file The file to upload
     * @param folder The S3 folder path (e.g., "documents")
     * @param auth The authenticated user uploading the file
     * @return The saved Document entity with metadata
     */
    fun uploadFile(file: MultipartFile, folder: String, auth: AuthenticatedUser): Document {
        val s3Key = "$folder/${UUID.randomUUID()}-${file.originalFilename}"

        // Upload to S3
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(s3Key)
            .contentType(file.contentType)
            .build()

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.bytes))

        // Fetch the authenticated user
        val user = userRepository.findById(auth.userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        // Save metadata to database
        val document = Document(
            organizationId = auth.requireOrganizationId(),
            s3Key = s3Key,
            fileName = file.originalFilename ?: "file",
            contentType = file.contentType ?: "application/octet-stream",
            uploadedByUser = user,
        )

        return documentRepository.save(document)
    }

    /**
     * Deletes a document from S3 and removes metadata from database.
     *
     * @param documentId The document ID
     * @param organizationId The organization ID (for scope verification)
     */
    fun deleteFile(documentId: Long, organizationId: Long) {
        val document = documentRepository.findByIdAndOrganizationId(documentId, organizationId)
            ?: throw IllegalArgumentException("Document not found")

        // Delete from S3
        val deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(document.s3Key)
            .build()

        s3Client.deleteObject(deleteObjectRequest)

        // Delete from database
        documentRepository.delete(document)
    }

    /**
     * Gets a document by ID with organization scope check.
     *
     * @param documentId The document ID
     * @param organizationId The organization ID (for scope verification)
     * @return The Document or null if not found
     */
    fun getDocument(documentId: Long, organizationId: Long): Document? {
        return documentRepository.findByIdAndOrganizationId(documentId, organizationId)
    }

    /**
     * Generates a presigned URL for accessing a document in S3.
     * The URL is valid for the specified duration (default: 1 hour).
     *
     * @param documentId The document ID
     * @param organizationId The organization ID (for scope verification)
     * @param duration How long the URL is valid (default 1 hour)
     * @return A presigned URL that can be used directly in the browser
     */
    fun getFileUrl(documentId: Long, organizationId: Long, duration: Duration = Duration.ofHours(1)): String {
        val document = getDocument(documentId, organizationId)
            ?: throw IllegalArgumentException("Document not found")

        val getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(document.s3Key)
            .build()

        val presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(duration)
            .getObjectRequest(getObjectRequest)
            .build()

        return s3Presigner.presignGetObject(presignRequest).url().toString()
    }
}