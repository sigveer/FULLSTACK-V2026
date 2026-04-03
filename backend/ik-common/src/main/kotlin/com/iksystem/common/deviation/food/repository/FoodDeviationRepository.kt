package com.iksystem.common.deviation.food.repository

import com.iksystem.common.deviation.food.model.FoodDeviation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FoodDeviationRepository : JpaRepository<FoodDeviation, Long> {
    fun findAllByOrganizationIdOrderByReportedAtDesc(organizationId: Long): List<FoodDeviation>
    fun findByIdAndOrganizationId(id: Long, organizationId: Long): FoodDeviation?
}
