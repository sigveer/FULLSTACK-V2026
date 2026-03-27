package com.iksystem.`ik-common`.user.repository

import com.iksystem.`ik-common`.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data JPA repository for [User] entities.
 *
 * Inherits standard CRUD and paging operations from [JpaRepository].
 * All custom queries are scoped by organization where appropriate.
 */
@Repository
interface UserRepository: JpaRepository<User, Long> {

    /**
     * Finds a user by their unique [email].
     *
     * @param email The email address to look up.
     * @return The matching [User], or `null` if none exists.
     */
    fun findByEmail(email: String): User?

    /**
     * Checks whether a user with the given [email] already exists.
     *
     * @param email The email address to check.
     * @return `true` if a user with that email exists, `false` otherwise.
     */
    fun existsByEmail(email: String): Boolean

    /**
     * Returns all users that belong to the given organization.
     *
     * @param organizationId The organization's primary key.
     * @return A list of [User] entities within that organization.
     */
    fun findAllByOrganizationId(organizationId: Long): List<User>

    /**
     * Finds a user by primary key, scoped to a specific organization.
     *
     * @param id The user's primary key.
     * @param organizationId The organization's primary key.
     * @return The matching [User], or `null` if not found in that organization.
     */
    fun findByIdAndOrganizationId(id: Long, organizationId: Long): User?
}