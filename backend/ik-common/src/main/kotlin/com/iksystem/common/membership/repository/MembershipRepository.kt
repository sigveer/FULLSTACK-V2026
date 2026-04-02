package com.iksystem.common.membership.repository

import com.iksystem.common.membership.model.Membership
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data JPA repository for [Membership] entities.
 */
@Repository
interface MembershipRepository : JpaRepository<Membership, Long> {

    /** Returns all memberships for a given user. */
    fun findAllByUserId(userId: Long): List<Membership>

    /** Returns all memberships in a given organization. */
    fun findAllByOrganizationId(organizationId: Long): List<Membership>

    /** Finds a specific membership by user and organization. */
    fun findByUserIdAndOrganizationId(userId: Long, organizationId: Long): Membership?

    /** Checks if a membership exists for a user in an organization. */
    fun existsByUserIdAndOrganizationId(userId: Long, organizationId: Long): Boolean
}
