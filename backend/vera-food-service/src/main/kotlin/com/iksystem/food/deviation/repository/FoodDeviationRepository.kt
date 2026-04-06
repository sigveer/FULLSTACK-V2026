package com.iksystem.food.deviation.repository

import com.iksystem.food.deviation.model.FoodDeviation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FoodDeviationRepository : JpaRepository<FoodDeviation, Long> {
    fun findAllByOrganizationIdOrderByReportedAtDesc(organizationId: Long): List<FoodDeviation>
    fun findByIdAndOrganizationId(id: Long, organizationId: Long): FoodDeviation?
}
