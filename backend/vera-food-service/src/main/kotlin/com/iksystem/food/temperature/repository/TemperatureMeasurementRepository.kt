package com.iksystem.food.temperature.repository

import com.iksystem.food.temperature.model.TemperatureMeasurement
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TemperatureMeasurementRepository : JpaRepository<TemperatureMeasurement, Long> {
    fun findAllByOrganizationIdOrderByMeasuredAtDesc(organizationId: Long): List<TemperatureMeasurement>
    fun findAllByOrganizationIdAndApplianceIdOrderByMeasuredAtDesc(organizationId: Long, applianceId: Long): List<TemperatureMeasurement>
    fun findTopByOrganizationIdAndApplianceIdOrderByMeasuredAtDesc(organizationId: Long, applianceId: Long): TemperatureMeasurement?
    fun findAllByOrganizationIdAndIdIn(organizationId: Long, ids: List<Long>): List<TemperatureMeasurement>
}
