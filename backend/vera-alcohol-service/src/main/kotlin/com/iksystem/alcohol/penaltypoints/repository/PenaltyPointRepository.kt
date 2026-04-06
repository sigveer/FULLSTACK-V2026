package com.iksystem.alcohol.penaltypoints.repository

import com.iksystem.alcohol.penaltypoints.model.PenaltyPoint
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PenaltyPointRepository : JpaRepository<PenaltyPoint, Long> {
    fun findAllByOrganizationIdOrderByCreatedAtDesc(organizationId: Long): List<PenaltyPoint>
    fun findByIdAndOrganizationId(id: Long, organizationId: Long): PenaltyPoint?

    @Query("SELECT COALESCE(SUM(p.points), 0) FROM PenaltyPoint p WHERE p.organizationId = :organizationId")
    fun sumPointsByOrganizationId(organizationId: Long): Int
}