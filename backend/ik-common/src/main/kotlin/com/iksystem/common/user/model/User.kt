package com.iksystem.common.user.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

/**
 * JPA entity representing a user identity in the system.
 *
 * Users are pure identity — roles and organization membership are
 * managed through the [Membership] entity. A user can belong to
 * multiple organizations with different roles.
 *
 * @property id Auto-generated primary key.
 * @property email Unique login email address.
 * @property password BCrypt-hashed password.
 * @property fullName User's display name.
 * @property phoneNumber Contact phone number.
 * @property active Whether the account is enabled; `false` prevents login.
 * @property createdAt Timestamp set automatically when the row is first inserted.
 */
@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Column(name = "full_name", nullable = false)
    val fullName: String,

    @Column(nullable = false)
    val phoneNumber: String,

    @Column(nullable = false)
    val active: Boolean = true,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
)
