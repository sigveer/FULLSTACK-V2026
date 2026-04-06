package com.iksystem.common.documents.repository

import com.iksystem.common.documents.model.Document
import org.springframework.data.jpa.repository.JpaRepository

interface DocumentRepository : JpaRepository<Document, Long> {
    fun findByIdAndOrganizationId(id: Long, organizationId: Long): Document?
    fun findByS3KeyAndOrganizationId(s3Key: String, organizationId: Long): Document?
}
