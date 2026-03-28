package com.iksystem.`ik-common`.organization.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

/**
 * JPA entity representing an organization in the system.
 *
 * Each organization has a unique [name] and an optional [orgNumber]
 * (e.g. a national business registry number).
 *
 * @property id Auto-generated primary key.
 * @property name Unique display name of the organization.
 * @property orgNumber Optional external organization/business number.
 * @property createdAt Timestamp set automatically when the row is first inserted.
 */
@Entity
@Table(name = "organizations")
data class Organization (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val name: String,

    @Column(name = "org_number", unique = true)
    val orgNumber: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
)