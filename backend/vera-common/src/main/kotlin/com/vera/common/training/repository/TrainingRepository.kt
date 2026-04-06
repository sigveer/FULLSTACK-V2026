package com.iksystem.common.training.repository

import com.iksystem.common.training.model.TrainingLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface TrainingRepository : JpaRepository<TrainingLog, Long> {

    fun findAllByOrganizationIdOrderByCreatedAtDesc(organizationId: Long): List<TrainingLog>

    fun findByIdAndOrganizationId(id: Long, organizationId: Long): TrainingLog?

    @Query("SELECT t FROM TrainingLog t WHERE t.expiresAt BETWEEN :now AND :future AND t.status != 'EXPIRED'")
    fun findExpiringSoon(now: Instant, future: Instant): List<TrainingLog>
}
