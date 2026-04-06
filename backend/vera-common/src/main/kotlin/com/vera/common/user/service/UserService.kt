package com.iksystem.common.user.service

import com.iksystem.common.exception.BadRequestException
import com.iksystem.common.exception.ConflictException
import com.iksystem.common.exception.ForbiddenException
import com.iksystem.common.exception.NotFoundException
import com.iksystem.common.membership.dto.MembershipResponse
import com.iksystem.common.membership.model.Membership
import com.iksystem.common.membership.repository.MembershipRepository
import com.iksystem.common.security.AuthenticatedUser
import com.iksystem.common.session.repository.SessionRepository
import com.iksystem.common.token.repository.RefreshTokenRepository
import com.iksystem.common.user.dto.CreateUserRequest
import com.iksystem.common.user.dto.UpdateUserRoleRequest
import com.iksystem.common.user.dto.UserResponse
import com.iksystem.common.user.model.Role
import com.iksystem.common.user.model.User
import com.iksystem.common.user.repository.UserRepository
import com.iksystem.common.organization.repository.OrganizationRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service layer for user management.
 *
 * Operations are scoped to the caller's organization via memberships.
 * "List users" means list all memberships in the caller's org.
 */
@Service
class UserService(
    private val userRepository: UserRepository,
    private val membershipRepository: MembershipRepository,
    private val organizationRepository: OrganizationRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val sessionRepository: SessionRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    /**
     * Lists all members in the caller's organization.
     */
    @Transactional(readOnly = true)
    fun listUsers(auth: AuthenticatedUser): List<MembershipResponse> {
        val orgId = auth.requireOrganizationId()
        return membershipRepository.findAllByOrganizationId(orgId).map { it.toMembershipResponse() }
    }

    /**
     * Gets a specific user's membership in the caller's organization.
     */
    @Transactional(readOnly = true)
    fun getUser(userId: Long, auth: AuthenticatedUser): MembershipResponse {
        val orgId = auth.requireOrganizationId()
        val membership = membershipRepository.findByUserIdAndOrganizationId(userId, orgId)
            ?: throw NotFoundException("User not found in this organization")
        return membership.toMembershipResponse()
    }

    /**
     * Returns the caller's own profile.
     */
    @Transactional(readOnly = true)
    fun getCurrentUser(auth: AuthenticatedUser): UserResponse {
        val user = userRepository.findById(auth.userId)
            .orElseThrow { NotFoundException("User not found") }
        return user.toResponse()
    }

    /**
     * Creates a new user and adds them to the caller's organization with the specified role.
     * If a user with that email already exists, adds them to the org instead.
     */
    @Transactional
    fun createUser(request: CreateUserRequest, auth: AuthenticatedUser): MembershipResponse {
        val orgId = auth.requireOrganizationId()
        val role = parseRole(request.role)

        val organization = organizationRepository.findById(orgId)
            .orElseThrow { NotFoundException("Organization not found") }

        var user = userRepository.findByEmail(request.email)

        if (user != null) {
            // User exists — check they're not already in this org
            if (membershipRepository.existsByUserIdAndOrganizationId(user.id, orgId)) {
                throw ConflictException("User is already a member of this organization")
            }
        } else {
            // Create new user identity
            user = userRepository.save(
                User(
                    email = request.email,
                    password = passwordEncoder.encode(request.password),
                    fullName = request.fullName,
                    phoneNumber = request.phoneNumber,
                )
            )
        }

        val membership = membershipRepository.save(
            Membership(user = user, organization = organization, role = role)
        )

        return membership.toMembershipResponse()
    }

    /**
     * Updates a user's role within the caller's organization.
     */
    @Transactional
    fun updateUserRole(userId: Long, request: UpdateUserRoleRequest, auth: AuthenticatedUser): MembershipResponse {
        val orgId = auth.requireOrganizationId()
        val membership = membershipRepository.findByUserIdAndOrganizationId(userId, orgId)
            ?: throw NotFoundException("User not found in this organization")

        val newRole = parseRole(request.role)
        val updated = membershipRepository.save(membership.copy(role = newRole))
        return updated.toMembershipResponse()
    }

    /**
     * Deactivates a user account globally (across all orgs).
     */
    @Transactional
    fun deactivateUser(userId: Long, auth: AuthenticatedUser): UserResponse {
        val orgId = auth.requireOrganizationId()
        membershipRepository.findByUserIdAndOrganizationId(userId, orgId)
            ?: throw NotFoundException("User not found in this organization")

        if (userId == auth.userId) {
            throw ForbiddenException("Cannot deactivate your own account")
        }

        val user = userRepository.findById(userId)
            .orElseThrow { NotFoundException("User not found") }

        refreshTokenRepository.revokeAllByUserId(user.id)
        sessionRepository.deactivateAllByUserId(user.id)

        val updated = userRepository.save(user.copy(active = false))
        return updated.toResponse()
    }

    /**
     * Re-activates a previously deactivated user account.
     */
    @Transactional
    fun activateUser(userId: Long, auth: AuthenticatedUser): UserResponse {
        val orgId = auth.requireOrganizationId()
        membershipRepository.findByUserIdAndOrganizationId(userId, orgId)
            ?: throw NotFoundException("User not found in this organization")

        val user = userRepository.findById(userId)
            .orElseThrow { NotFoundException("User not found") }

        val updated = userRepository.save(user.copy(active = true))
        return updated.toResponse()
    }

    /**
     * Kicks a user by revoking their sessions and tokens.
     */
    @Transactional
    fun kickUser(userId: Long, auth: AuthenticatedUser) {
        val orgId = auth.requireOrganizationId()
        membershipRepository.findByUserIdAndOrganizationId(userId, orgId)
            ?: throw NotFoundException("User not found in this organization")

        if (userId == auth.userId) {
            throw ForbiddenException("Cannot kick yourself")
        }

        refreshTokenRepository.revokeAllByUserId(userId)
        sessionRepository.deactivateAllByUserId(userId)
    }

    /**
     * Removes a user's membership from the caller's organization.
     */
    @Transactional
    fun removeMember(userId: Long, auth: AuthenticatedUser) {
        val orgId = auth.requireOrganizationId()
        val membership = membershipRepository.findByUserIdAndOrganizationId(userId, orgId)
            ?: throw NotFoundException("User not found in this organization")

        if (userId == auth.userId) {
            throw ForbiddenException("Cannot remove yourself from the organization")
        }

        membershipRepository.delete(membership)
        // Revoke org-scoped tokens
        refreshTokenRepository.revokeAllByUserIdAndOrganizationId(userId, orgId)
        sessionRepository.deactivateAllByUserIdAndOrganizationId(userId, orgId)
    }

    private fun parseRole(role: String): Role =
        try {
            Role.valueOf(role.uppercase())
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("Invalid role: $role. Must be one of: ADMIN, MANAGER, EMPLOYEE")
        }
}

/** Extension function that maps a [User] entity to a [UserResponse] DTO. */
fun User.toResponse() = UserResponse(
    id = id,
    email = email,
    fullName = fullName,
    phoneNumber = phoneNumber,
    active = active,
)

/** Extension function that maps a [Membership] to a [MembershipResponse] DTO. */
fun Membership.toMembershipResponse() = MembershipResponse(
    id = id,
    userId = user.id,
    userEmail = user.email,
    userFullName = user.fullName,
    organizationId = organization.id,
    role = role.name,
)
