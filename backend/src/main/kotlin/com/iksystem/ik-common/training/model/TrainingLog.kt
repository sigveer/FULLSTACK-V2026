package com.iksystem.`ik-common`.training.model

import com.iksystem.`ik-common`.user.model.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "training_logs")
data class TrainingLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "organization_id", nullable = false)
    val organizationId: Long,

    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_user_id", nullable = false)
    val employeeUser: User,

    @ManyToOne(optional = false)
    @JoinColumn(name = "logged_by_user_id", nullable = false)
    val loggedByUser: User,

    @Column(nullable = false)
    val title: String,

    @Column(columnDefinition = "TEXT")
    val description: String? = null,

    @Column(name = "completed_at")
    val completedAt: Instant? = null,

    @Column(name = "expires_at")
    val expiresAt: Instant? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: TrainingStatus = TrainingStatus.NOT_COMPLETED,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now(),
)
