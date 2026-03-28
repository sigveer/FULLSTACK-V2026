package com.iksystem.`ik-common`.session.model

import com.iksystem.`ik-common`.user.model.User
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
 * JPA entity representing an active user session.
 *
 * Sessions track where and when a user authenticated. They can be
 * soft-disabled via [active] (e.g. when a user is kicked or deactivated)
 * and are automatically cleaned up after [expiresAt].
 *
 * @property id Auto-generated primary key.
 * @property sessionId Unique opaque session identifier sent to the client.
 * @property user The user who owns this session.
 * @property organizationId The organization the user belonged to at login time.
 * @property ipAddress IP address of the client that created the session, if available.
 * @property userAgent HTTP User-Agent header captured at login, if available.
 * @property expiresAt Absolute expiration timestamp; expired sessions may be purged.
 * @property active Whether the session is still valid; `false` means it has been revoked.
 * @property createdAt Timestamp set automatically when the row is first inserted.
 */
@Entity
@Table(name = "sessions")
data class Session(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "session_id", nullable = false, unique = true)
    val sessionId: String,

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "organization_id", nullable = false)
    val organizationId: Long,

    @Column(name = "ip_address")
    val ipAddress: String? = null,

    @Column(name = "user_agent")
    val userAgent: String? = null,

    @Column(name = "expires_at", nullable = false)
    val expiresAt: Instant,

    @Column(nullable = false)
    val active: Boolean = true,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
)
