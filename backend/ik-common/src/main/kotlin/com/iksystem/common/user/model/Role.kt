package com.iksystem.common.user.model

/**
 * Roles available within an organization.
 *
 * Used to control access to endpoints via Spring Security's `@PreAuthorize`.
 */
enum class Role {
    /** Full administrative access to the organization. */
    ADMIN,
    /** Can manage users and view reports but cannot change organization settings. */
    MANAGER,
    /** Standard user with limited permissions. */
    EMPLOYEE,
}