package com.iksystem.common.deviation.alcohol.model

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "penalty_points")
data class PenaltyPoint(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "organization_id", nullable = false)
    val organizationId: Long,

    @ManyToOne
    @JoinColumn(name = "alcohol_deviation_id")
    val alcoholDeviation: AlcoholDeviation? = null,

    @Column(nullable = false)
    val points: Int,

    @Enumerated(EnumType.STRING)
    @Column(name = "violation_type", nullable = false)
    val violationType: AlcoholDeviationType,

    @Column(length = 500)
    val description: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
)
