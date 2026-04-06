package com.iksystem.common.invite.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "invitations")
data class Invitation (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "organization_id", nullable = false)
    val organizationId: Long,

    @Column(nullable = false)
    val email: String,

    @Column(nullable = false)
    val role: String,

    @Column(nullable = false)
    val token: String,

    @Column(name = "invited_by_id", nullable = false)
    val invitedById: Long,

    @Column(name = "expires_at", nullable = false)
    val expiresAt: Instant,

    @Column(name = "accepted_at")
    var acceptedAt: Instant? = null,
)