package com.iksystem.common.security

import com.iksystem.common.membership.model.Membership
import com.iksystem.common.user.model.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import javax.crypto.SecretKey
import java.util.Date
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.Claims

/**
 * Service responsible for JWT creation and validation.
 *
 * Supports two token types:
 * - **Pre-auth token**: issued after login, contains only userId + email.
 *   Used to call `/auth/select-org`. Has no role or orgId claims.
 * - **Full token**: issued after org selection, contains userId, email, role, orgId.
 *   Used for all authenticated API calls.
 */
@Service
class JwtService(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.access-token-expiration}") private val accessTokenExpiration: Long
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    /**
     * Generates a pre-auth JWT (no role/orgId) valid for 5 minutes.
     * Used between login and organization selection.
     */
    fun generatePreAuthToken(user: User): String {
        val now = Date()
        val expiry = Date(now.time + 300_000) // 5 minutes

        return Jwts.builder()
            .subject(user.id.toString())
            .claim("email", user.email)
            .claim("type", "pre_auth")
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()
    }

    /**
     * Generates a full JWT access token scoped to a specific organization membership.
     */
    fun generateAccessToken(user: User, membership: Membership): String {
        val now = Date()
        val expiry = Date(now.time + accessTokenExpiration)

        return Jwts.builder()
            .subject(user.id.toString())
            .claim("email", user.email)
            .claim("role", membership.role.name)
            .claim("orgId", membership.organization.id)
            .claim("type", "access")
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()
    }

    /** Validates a JWT and returns its claims, or `null` if invalid. */
    fun validateToken(token: String): Claims? =
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: Exception) {
            null
        }

    /** Extracts the user ID from the token's `subject` claim. */
    fun getUserId(claims: Claims): Long = claims.subject.toLong()

    /** Extracts the token type ("pre_auth" or "access"). */
    fun getTokenType(claims: Claims): String = claims["type"] as? String ?: "access"

    /** Extracts the user's role from the `role` claim. Returns null for pre-auth tokens. */
    fun getRole(claims: Claims): String? = claims["role"] as? String

    /** Extracts the organization ID from the `orgId` claim. Returns null for pre-auth tokens. */
    fun getOrganizationId(claims: Claims): Long? = (claims["orgId"] as? Number)?.toLong()
}
