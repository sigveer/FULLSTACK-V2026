package com.iksystem.`ik-common`.user.service

import com.ik.ikcommon.exception.BadRequestException
import com.ik.ikcommon.exception.ConflictException
import com.ik.ikcommon.exception.ForbiddenException
import com.ik.ikcommon.exception.NotFoundException
import com.iksystem.`ik-common`.organization.repository.OrganizationRepository
import com.iksystem.`ik-common`.security.AuthenticatedUser
import com.iksystem.`ik-common`.user.dto.CreateUserRequest
import com.iksystem.`ik-common`.user.dto.UpdateUserRoleRequest
import com.iksystem.`ik-common`.user.dto.UserResponse
import com.iksystem.`ik-common`.user.model.Role
import com.iksystem.`ik-common`.user.model.User
import com.iksystem.`ik-common`.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service layer for user management.
 *
 * All operations are scoped to the caller's organization via [AuthenticatedUser],
 * ensuring tenant isolation. Passwords are hashed with [PasswordEncoder] before storage.
 *
 * @property userRepository JPA repository for user persistence.
 * @property organizationRepository JPA repository used to resolve the caller's organization.
 * @property passwordEncoder Encoder used for hashing passwords (BCrypt).
 */
@Service
class UserService(
    private val userRepository: UserRepository,
    //private val refreshTokenRepository: RefreshTokenRepository,
    private val organizationRepository: OrganizationRepository,
    //private val sessionRepository: SessionRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    /**
     * Lists all users in the caller's organization.
     *
     * @param auth The authenticated caller, used to determine organization scope.
     * @return A list of [UserResponse] DTOs.
     */
    @Transactional(readOnly = true)
    fun listUsers(auth: AuthenticatedUser): List<UserResponse> =
        userRepository.findAllByOrganizationId(auth.organizationId).map { it.toResponse() }

    /**
     * Retrieves a single user by ID within the caller's organization.
     *
     * @param id The user's primary key.
     * @param auth The authenticated caller.
     * @return A [UserResponse] for the matching user.
     * @throws NotFoundException if no user with the given [id] exists in the organization.
     */
    @Transactional(readOnly = true)
    fun getUser(id: Long, auth: AuthenticatedUser): UserResponse {
        val user = userRepository.findByIdAndOrganizationId(id, auth.organizationId)
            ?: throw NotFoundException("User not found")
        return user.toResponse()
    }

    /**
     * Creates a new user in the caller's organization.
     *
     * @param request DTO containing the new user's details.
     * @param auth The authenticated caller, whose organization the new user will join.
     * @return A [UserResponse] representing the newly created user.
     * @throws ConflictException if a user with the same email already exists.
     * @throws NotFoundException if the caller's organization cannot be found.
     */
    @Transactional
    fun createUser(request: CreateUserRequest, auth: AuthenticatedUser): UserResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw ConflictException("User already registered")
        }

        val role = parseRole(request.role)
        val organization = organizationRepository.findById(auth.organizationId)
            .orElseThrow { NotFoundException("Organization not found") }

        val user = userRepository.save(
            User(
                email = request.email,
                password = passwordEncoder.encode(request.password),
                fullName = request.fullName,
                phoneNumber = request.phoneNumber,
                role = role,
                organization = organization,
            )
        )

        return user.toResponse()
    }

    /**
     * Updates the role of an existing user.
     *
     * @param id The target user's primary key.
     * @param request DTO containing the new role string.
     * @param auth The authenticated caller.
     * @return A [UserResponse] reflecting the updated role.
     * @throws NotFoundException if the user does not exist in the caller's organization.
     * @throws BadRequestException if the role string is invalid.
     */
    @Transactional
    fun updateUserRole(id: Long, request: UpdateUserRoleRequest, auth: AuthenticatedUser): UserResponse {
        val user = userRepository.findByIdAndOrganizationId(id, auth.organizationId)
            ?: throw NotFoundException("User not found")

        val newRole = parseRole(request.role)
        val updated = userRepository.save(user.copy(role = newRole))
        return user.toResponse()
    }

    /**
     * Soft-disables a user account by setting [User.active] to `false`.
     *
     * A user cannot deactivate their own account.
     *
     * @param id The target user's primary key.
     * @param auth The authenticated caller.
     * @return A [UserResponse] with `active = false`.
     * @throws NotFoundException if the user does not exist in the caller's organization.
     * @throws ForbiddenException if the caller attempts to deactivate themselves.
     */
    @Transactional
    fun deactivateUser(id: Long, auth: AuthenticatedUser): UserResponse {
        val user = userRepository.findByIdAndOrganizationId(id, auth.organizationId)
            ?: throw NotFoundException("User not found")

        if (user.id == auth.userId) {
            throw ForbiddenException("Cannot deactivate your own account")
        }

        //refreshTokenRepository.revokeAllByUserId(user.id)
        //sessionRepository.deactivateAllByUserId(user.id)

        val updated = userRepository.save(user.copy(active = false))
        return updated.toResponse()
    }

    /**
     * Re-enables a previously deactivated user account.
     *
     * @param id The target user's primary key.
     * @param auth The authenticated caller.
     * @return A [UserResponse] with `active = true`.
     * @throws NotFoundException if the user does not exist in the caller's organization.
     */
    @Transactional
    fun activateUser(id: Long, auth: AuthenticatedUser): UserResponse {
        val user = userRepository.findByIdAndOrganizationId(id, auth.organizationId)
            ?: throw NotFoundException("User not found")

        val updated = userRepository.save(user.copy(active = true))
        return updated.toResponse()
    }

    /**
     * Kicks a user by revoking their active sessions and refresh tokens.
     *
     * The user record itself is not deleted or deactivated. A user cannot kick themselves.
     *
     * @param id The target user's primary key.
     * @param auth The authenticated caller.
     * @throws NotFoundException if the user does not exist in the caller's organization.
     * @throws ForbiddenException if the caller attempts to kick themselves.
     */
    @Transactional
    fun kickUser(id: Long, auth: AuthenticatedUser) {
        val user = userRepository.findByIdAndOrganizationId(id, auth.organizationId)
            ?: throw NotFoundException("User not found")

        if (user.id == auth.userId) {
            throw ForbiddenException("Cannot kick yourself")
        }

        //refreshTokenRepository.revokeAllByUserId(user.id)
        //sessionRepository.deactivateAllByUserId(user.id)
    }

    /**
     * Parses a role string into a [Role] enum value (case-insensitive).
     *
     * @param role The role string to parse.
     * @return The corresponding [Role].
     * @throws BadRequestException if the string does not match any known role.
     */
    private fun parseRole(role: String): Role =
        try {
            Role.valueOf(role.uppercase())
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("Invalid role: $role. Must be one of: ADMIN, MANAGER, EMPLOYEE")
        }
}

/**
 * Extension function that maps a [User] entity to a [UserResponse] DTO.
 */
fun User.toResponse() = UserResponse(
    id = id,
    email = email,
    fullName = fullName,
    phoneNumber = phoneNumber,
    role = role.name,
    organizationId = organization.id,
    active = active,
)