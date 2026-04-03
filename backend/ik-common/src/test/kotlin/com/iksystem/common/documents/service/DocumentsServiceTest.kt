package com.iksystem.common.documents.service

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.mock.web.MockMultipartFile
import software.amazon.awssdk.services.s3.S3Client

class DocumentsServiceTest : FunSpec({
    val s3Client = mockk<S3Client>(relaxed = true)
    val bucketName = "test-bucket"
    val service = DocumentsService(s3Client, bucketName)

    beforeTest {
        clearMocks(s3Client)
    }

    test("uploadFile returns key with folder prefix") {
        val file = MockMultipartFile("file", "test.txt", "text/plain", "content".toByteArray())
        val key = service.uploadFile(file, "documents")
        key.shouldContain("documents/")
        key.shouldContain("test.txt")
    }

    test("deleteFile calls s3Client.deleteObject") {
        val key = "documents/test.txt"
        service.deleteFile(key)
        verify { s3Client.deleteObject(any<software.amazon.awssdk.services.s3.model.DeleteObjectRequest>()) }
    }

    test("getFileUrl returns correct S3 URL") {
        val key = "documents/file.txt"
        val url = service.getFileUrl(key)
        url shouldBe "https://$bucketName.s3.amazonaws.com/$key"
    }

    test("test() calls listBuckets") {
        every { s3Client.listBuckets() } returns mockk()
        service.test()
        verify { s3Client.listBuckets() }
    }
})
