package com.iksystem.common.membership.model

import com.iksystem.common.organization.model.Organization
import com.iksystem.common.user.model.Role
import com.iksystem.common.user.model.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

/**
 * JPA entity representing a user's membership in an organization.
 *
 * A user can belong to multiple organizations, each with a different [Role].
 * The combination of [user] and [organization] is unique.
 *
 * @property id Auto-generated primary key.
 * @property user The user who holds this membership.
 * @property organization The organization the user belongs to.
 * @property role The user's role within this organization.
 * @property createdAt Timestamp set automatically when the membership is created.
 */
@Entity
@Table(name = "memberships")
data class Membership(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    val organization: Organization,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: Role = Role.EMPLOYEE,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
)
