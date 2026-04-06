package com.iksystem.food.temperature.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "temperature_appliances")
data class TemperatureAppliance(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "organization_id", nullable = false)
    val organizationId: Long,

    @Column(nullable = false, length = 120)
    val name: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "appliance_type", nullable = false, length = 20)
    val applianceType: TemperatureApplianceType,

    @Column(name = "min_temperature", nullable = false, precision = 6, scale = 2)
    val minTemperature: BigDecimal,

    @Column(name = "max_temperature", nullable = false, precision = 6, scale = 2)
    val maxTemperature: BigDecimal,

    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = true,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now(),
)
