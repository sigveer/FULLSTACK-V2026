package com.iksystem.common.deviation.food.model

import com.iksystem.common.user.model.User
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "food_deviations")
data class FoodDeviation(
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
    @Column(name = "deviation_type", nullable = false)
    val deviationType: FoodDeviationType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val severity: com.iksystem.common.deviation.model.DeviationSeverity,

    @Column(columnDefinition = "TEXT", nullable = false)
    val description: String,

    @Column(name = "immediate_action", columnDefinition = "TEXT")
    val immediateAction: String? = null,

    @ManyToOne
    @JoinColumn(name = "immediate_action_by_user_id")
    val immediateActionByUser: User? = null,

    @Column(name = "immediate_action_at")
    val immediateActionAt: Instant? = null,

    @Column(columnDefinition = "TEXT")
    val cause: String? = null,

    @Column(name = "preventive_measures", columnDefinition = "TEXT")
    val preventiveMeasures: String? = null,

    @ManyToOne
    @JoinColumn(name = "preventive_responsible_user_id")
    val preventiveResponsibleUser: User? = null,

    @Column(name = "preventive_deadline")
    val preventiveDeadline: Instant? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: FoodDeviationStatus = FoodDeviationStatus.OPEN,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now(),
)
