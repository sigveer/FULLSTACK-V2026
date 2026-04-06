package com.iksystem.common.user.repository

import com.iksystem.common.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data JPA repository for [User] entities.
 *
 * Users are now pure identity — organization scoping is done through memberships.
 */
@Repository
interface UserRepository : JpaRepository<User, Long> {

    /** Finds a user by their unique email. */
    fun findByEmail(email: String): User?

    /** Checks whether a user with the given email already exists. */
    fun existsByEmail(email: String): Boolean
}
