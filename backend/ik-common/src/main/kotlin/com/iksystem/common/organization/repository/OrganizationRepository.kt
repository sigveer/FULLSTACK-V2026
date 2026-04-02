package com.iksystem.common.organization.repository

import com.iksystem.common.organization.model.Organization
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data JPA repository for [Organization] entities.
 *
 * Inherits standard CRUD and paging operations from [JpaRepository].
 */
@Repository
interface OrganizationRepository : JpaRepository<Organization, Long> {

    /**
     * Finds an organization by its exact [name].
     *
     * @param name The unique organization name to search for.
     * @return The matching [Organization], or `null` if none exists.
     */
    fun findByName(name: String): Organization?

    /**
     * Checks whether an organization with the given [name] already exists.
     *
     * @param name The organization name to check.
     * @return `true` if an organization with that name exists, `false` otherwise.
     */
    fun existsByName(name: String): Boolean
}