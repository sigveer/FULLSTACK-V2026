package com.iksystem.common.token.repository

import com.iksystem.common.token.model.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Spring Data JPA repository for [RefreshToken] entities.
 *
 * Provides token lookup as well as bulk operations for revoking
 * and cleaning up refresh tokens.
 */
@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {

    /**
     * Finds a refresh token by its unique opaque [token] string.
     *
     * @param token The token value sent by the client.
     * @return The matching [RefreshToken], or `null` if none exists.
     */
    fun findByToken(token: String): RefreshToken?

    /**
     * Revokes every refresh token belonging to the specified user.
     *
     * Uses a bulk JPQL UPDATE because Spring Data cannot derive
     * modification queries from method names alone.
     *
     * @param userId The user whose tokens should be revoked.
     */
    @Modifying
    @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.user.id = :userId")
    fun revokeAllByUserId(@Param("userId") userId: Long)

    /**
     * Revokes all refresh tokens for a user within a specific organization.
     *
     * Used when a user switches orgs or is removed from an organization,
     * so only tokens scoped to that org are invalidated.
     *
     * @param userId The user's primary key.
     * @param organizationId The organization whose tokens should be revoked.
     */
    @Modifying
    @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.user.id = :userId AND r.organizationId = :organizationId")
    fun revokeAllByUserIdAndOrganizationId(@Param("userId") userId: Long, @Param("organizationId") organizationId: Long)

    /**
     * Deletes all refresh tokens whose [RefreshToken.expiresAt] is in the past.
     *
     * Intended to be called by a scheduled cleanup job.
     */
    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.expiresAt < CURRENT_TIMESTAMP")
    fun deleteExpired()
}
