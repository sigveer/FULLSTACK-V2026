package com.iksystem.food.temperature.model

import com.iksystem.common.user.model.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "temperature_measurements")
data class TemperatureMeasurement(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "organization_id", nullable = false)
    val organizationId: Long,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appliance_id", nullable = false)
    val appliance: TemperatureAppliance,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "measured_by_user_id", nullable = false)
    val measuredByUser: User,

    @Column(name = "measured_at", nullable = false)
    val measuredAt: Instant = Instant.now(),

    @Column(nullable = false, precision = 6, scale = 2)
    val temperature: BigDecimal,

    @Column(columnDefinition = "TEXT")
    val note: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val status: TemperatureMeasurementStatus,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
)
