package com.iksystem.common.documents.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import java.util.UUID

@Service
class DocumentsService(private val s3Client: S3Client, @Value("\${aws.s3.bucket}") val bucketName: String) {
    fun test() {
        s3Client.listBuckets()
    }

    fun uploadFile(file: MultipartFile, folder: String): String {
        val key = "$folder/${UUID.randomUUID()}-${file.originalFilename}"

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(file.contentType)
            .build()

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.bytes))

        return key
    }

    fun deleteFile(key: String) {
        val deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build()

        s3Client.deleteObject(deleteObjectRequest)
    }

    fun getFileUrl(key: String): String {
        return "https://$bucketName.s3.amazonaws.com/$key"
    }
}