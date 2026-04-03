package com.iksystem.common.deviation.alcohol.model

import com.iksystem.common.user.model.User
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "alcohol_deviations")
data class AlcoholDeviation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "organization_id", nullable = false)
    val organizationId: Long,

    @Column(name = "reported_at", nullable = false)
    val reportedAt: Instant = Instant.now(),

    @ManyToOne(optional = false)
    @JoinColumn(name = "reported_by_user_id", nullable = false)
    val reportedByUser: User,

    @Enumerated(EnumType.STRING)
    @Column(name = "report_source", nullable = false)
    val reportSource: AlcoholReportSource,

    @Enumerated(EnumType.STRING)
    @Column(name = "deviation_type", nullable = false)
    val deviationType: AlcoholDeviationType,

    @Column(columnDefinition = "TEXT", nullable = false)
    val description: String,

    @Column(name = "immediate_action", columnDefinition = "TEXT")
    val immediateAction: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "causal_analysis")
    val causalAnalysis: AlcoholCausalAnalysis? = null,

    @Column(name = "causal_explanation", columnDefinition = "TEXT")
    val causalExplanation: String? = null,

    @Column(name = "preventive_measures", columnDefinition = "TEXT")
    val preventiveMeasures: String? = null,

    @Column(name = "preventive_deadline")
    val preventiveDeadline: Instant? = null,

    @ManyToOne
    @JoinColumn(name = "preventive_responsible_user_id")
    val preventiveResponsibleUser: User? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: AlcoholDeviationStatus = AlcoholDeviationStatus.OPEN,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now(),
)
