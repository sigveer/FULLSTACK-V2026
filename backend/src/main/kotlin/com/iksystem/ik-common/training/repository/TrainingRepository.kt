package com.iksystem.`ik-common`.training.repository

import com.iksystem.`ik-common`.training.model.TrainingLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TrainingRepository : JpaRepository<TrainingLog, Long> {

    fun findAllByOrganizationIdOrderByCreatedAtDesc(organizationId: Long): List<TrainingLog>

    fun findByIdAndOrganizationId(id: Long, organizationId: Long): TrainingLog?
}
