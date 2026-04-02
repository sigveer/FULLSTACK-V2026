package com.iksystem.common.checklist.repository

import com.iksystem.common.checklist.model.Checklist
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ChecklistRepository : JpaRepository<Checklist, Long> {
    fun findAllByOrganizationIdOrderByCreatedAtDesc(organizationId: Long): List<Checklist>

    fun findByIdAndOrganizationId(id: Long, organizationId: Long): Checklist?

    @Query(
        """
        SELECT COUNT(c.id)
        FROM Checklist c
        WHERE c.organizationId = :organizationId
          AND c.active = true
          AND (
                NOT EXISTS (
                    SELECT ciAll.id
                    FROM ChecklistItem ciAll
                    WHERE ciAll.checklist.id = c.id
                )
                OR EXISTS (
                    SELECT ciOpen.id
                    FROM ChecklistItem ciOpen
                    WHERE ciOpen.checklist.id = c.id
                      AND ciOpen.completed = false
                )
          )
        """
    )
    fun countActiveOpenByOrganizationId(@Param("organizationId") organizationId: Long): Long
}
