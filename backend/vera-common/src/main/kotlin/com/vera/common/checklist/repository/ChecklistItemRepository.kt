package com.iksystem.common.checklist.repository

import com.iksystem.common.checklist.model.ChecklistItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface ChecklistItemRepository : JpaRepository<ChecklistItem, Long> {
    fun findAllByChecklistIdOrderByIdAsc(checklistId: Long): List<ChecklistItem>

    fun findByIdAndChecklistId(id: Long, checklistId: Long): ChecklistItem?

    @Query(
        """
        SELECT COUNT(ci.id)
        FROM ChecklistItem ci
        WHERE ci.checklist.organizationId = :organizationId
        """
    )
    fun countAllByOrganizationId(@Param("organizationId") organizationId: Long): Long

    fun countByChecklistIdAndCompletedFalse(checklistId: Long): Long

    @Modifying
    @Query(
        """
        UPDATE ChecklistItem ci
        SET ci.completed = :completed,
            ci.completedAt = :completedAt,
            ci.updatedAt = :updatedAt
        WHERE ci.checklist.id = :checklistId
        """
    )
    fun setChecklistCompletion(
        @Param("checklistId") checklistId: Long,
        @Param("completed") completed: Boolean,
        @Param("completedAt") completedAt: Instant?,
        @Param("updatedAt") updatedAt: Instant,
    ): Int
}
