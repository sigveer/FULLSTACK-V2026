package com.iksystem.food.temperature.repository

import com.iksystem.food.temperature.model.TemperatureAppliance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TemperatureApplianceRepository : JpaRepository<TemperatureAppliance, Long> {
    fun findAllByOrganizationIdOrderByNameAsc(organizationId: Long): List<TemperatureAppliance>
    fun findByIdAndOrganizationId(id: Long, organizationId: Long): TemperatureAppliance?
}
