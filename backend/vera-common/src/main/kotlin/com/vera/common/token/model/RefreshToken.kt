package com.iksystem.common.token.model

import com.iksystem.common.user.model.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

/**
 * JPA entity representing a refresh token issued during authentication.
 *
 * Refresh tokens allow clients to obtain new access tokens without
 * re-entering credentials. A token can be individually [revoked] or
 * bulk-revoked when a user is kicked/deactivated. Expired tokens
 * are cleaned up via a scheduled purge.
 *
 * @property id Auto-generated primary key.
 * @property token Unique opaque token string sent to the client.
 * @property user The user this refresh token was issued to.
 * @property organizationId The organization the user belonged to at issuance time.
 * @property expiresAt Absolute expiration timestamp; the token is invalid after this point.
 * @property revoked Whether the token has been explicitly revoked before expiry.
 * @property createdAt Timestamp set automatically when the row is first inserted.
 */
@Entity
@Table(name = "refresh_tokens")
data class RefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val token: String,

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "organization_id", nullable = false)
    val organizationId: Long,

    @Column(name = "expires_at", nullable = false)
    val expiresAt: Instant,

    @Column(nullable = false)
    val revoked: Boolean = false,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
)
