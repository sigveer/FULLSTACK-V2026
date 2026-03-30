package com.iksystem.`ik-common`.deviation.model

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

/**
 * Deviation (avvik) reported from IK-Mat or IK-Alkohol workflows.
 */
@Entity
@Table(name = "deviations")
data class Deviation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "organization_id", nullable = false)
    val organizationId: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val module: DeviationModule,

    @Column(nullable = false)
    val title: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    val description: String,

    @Column(name = "immediate_action", columnDefinition = "TEXT")
    val immediateAction: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val severity: DeviationSeverity,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: DeviationStatus = DeviationStatus.OPEN,

    @ManyToOne(optional = false)
    @JoinColumn(name = "reported_by_user_id", nullable = false)
    val reportedByUser: User,

    @ManyToOne
    @JoinColumn(name = "assigned_to_user_id")
    val assignedToUser: User? = null,

    @Column(name = "reported_at", nullable = false)
    val reportedAt: Instant = Instant.now(),

    @Column(name = "resolved_at")
    val resolvedAt: Instant? = null,

    @Column(name = "closed_at")
    val closedAt: Instant? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now(),
)
