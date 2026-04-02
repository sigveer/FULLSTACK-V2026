package com.iksystem.common.security

/**
 * Principal object placed into the Spring Security context after JWT validation.
 *
 * For pre-auth tokens (between login and org selection), [organizationId] and [role]
 * will be `null`. For full access tokens, all fields are populated.
 *
 * @property userId The authenticated user's primary key.
 * @property organizationId The selected organization, or `null` for pre-auth tokens.
 * @property role The user's role in the selected org, or `null` for pre-auth tokens.
 */
class AuthenticatedUser(
    val userId: Long,
    val organizationId: Long?,
    val role: String?,
) {
    /** Returns true if this is a fully authenticated token with org context. */
    val isFullyAuthenticated: Boolean get() = organizationId != null && role != null

    /** Returns the org ID, throwing if not yet selected. */
    fun requireOrganizationId(): Long =
        organizationId ?: throw IllegalStateException("Organization not selected")

    /** Returns the role, throwing if not yet selected. */
    fun requireRole(): String =
        role ?: throw IllegalStateException("Organization not selected")
}
