package com.iksystem.food.temperature.dto

import com.iksystem.food.temperature.model.TemperatureApplianceType
import com.iksystem.food.temperature.model.TemperatureMeasurementStatus
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class CreateTemperatureApplianceRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(max = 120)
    val name: String,

    @field:NotNull(message = "Appliance type is required")
    val applianceType: TemperatureApplianceType,

    @field:NotNull(message = "Min temperature is required")
    @field:DecimalMin(value = "-100.00")
    @field:DecimalMax(value = "200.00")
    val minTemperature: BigDecimal,

    @field:NotNull(message = "Max temperature is required")
    @field:DecimalMin(value = "-100.00")
    @field:DecimalMax(value = "200.00")
    val maxTemperature: BigDecimal,
)

data class UpdateTemperatureApplianceRequest(
    @field:Size(max = 120)
    val name: String? = null,

    val minTemperature: BigDecimal? = null,
    val maxTemperature: BigDecimal? = null,
    val isActive: Boolean? = null,
)

data class RegisterTemperatureMeasurementRequest(
    @field:NotNull(message = "Appliance is required")
    val applianceId: Long,

    @field:NotNull(message = "Temperature is required")
    @field:DecimalMin(value = "-100.00")
    @field:DecimalMax(value = "200.00")
    val temperature: BigDecimal,

    @field:Size(max = 5000)
    val note: String? = null,

    val measuredAt: String? = null,
)

data class DeleteTemperatureMeasurementsRequest(
    @field:NotNull
    val ids: List<Long>,
)

data class TemperatureMeasurementResponse(
    val id: Long,
    val organizationId: Long,
    val applianceId: Long,
    val applianceName: String,
    val applianceType: TemperatureApplianceType,
    val measuredByUserId: Long,
    val measuredByUserName: String,
    val measuredAt: String,
    val temperature: BigDecimal,
    val note: String?,
    val status: TemperatureMeasurementStatus,
    val createdAt: String,
)

data class TemperatureApplianceResponse(
    val id: Long,
    val organizationId: Long,
    val name: String,
    val applianceType: TemperatureApplianceType,
    val minTemperature: BigDecimal,
    val maxTemperature: BigDecimal,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val lastMeasurement: TemperatureMeasurementResponse? = null,
)
