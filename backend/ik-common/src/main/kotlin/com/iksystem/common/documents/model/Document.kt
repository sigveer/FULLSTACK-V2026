package com.iksystem.common.documents.model

import com.iksystem.common.user.model.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

/**
 * Document metadata stored in S3 with reference in database.
 * Scoped to organization to ensure data isolation.
 */
@Entity
@Table(name = "documents")
data class Document(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "organization_id", nullable = false)
    val organizationId: Long,

    @Column(name = "s3_key", nullable = false)
    val s3Key: String,

    @Column(nullable = false)
    val fileName: String,

    @Column(name = "content_type", nullable = false)
    val contentType: String,

    @ManyToOne(optional = false)
    @JoinColumn(name = "uploaded_by_user_id", nullable = false)
    val uploadedByUser: User,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now(),
)
