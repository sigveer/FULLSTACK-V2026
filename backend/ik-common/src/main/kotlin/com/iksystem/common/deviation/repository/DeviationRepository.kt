package com.iksystem.common.deviation.repository

import com.iksystem.common.deviation.model.Deviation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DeviationRepository : JpaRepository<Deviation, Long> {
    fun findAllByOrganizationIdOrderByReportedAtDesc(organizationId: Long): List<Deviation>

    fun findByIdAndOrganizationId(id: Long, organizationId: Long): Deviation?
}
