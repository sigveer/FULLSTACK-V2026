package com.iksystem.common.auth.service

import com.iksystem.common.exception.ConflictException
import com.iksystem.common.exception.ForbiddenException
import com.iksystem.common.exception.NotFoundException
import com.iksystem.common.exception.UnauthorizedException
import com.iksystem.common.auth.dto.AuthResponse
import com.iksystem.common.auth.dto.LoginRequest
import com.iksystem.common.auth.dto.LoginResponse
import com.iksystem.common.auth.dto.RefreshRequest
import com.iksystem.common.auth.dto.RegisterRequest
import com.iksystem.common.auth.dto.SelectOrgRequest
import com.iksystem.common.membership.dto.MembershipSummary
import com.iksystem.common.membership.model.Membership
import com.iksystem.common.membership.repository.MembershipRepository
import com.iksystem.common.resend.service.ResendService
import com.iksystem.common.security.JwtService
import com.iksystem.common.session.model.Session
import com.iksystem.common.session.repository.SessionRepository
import com.iksystem.common.token.model.RefreshToken
import com.iksystem.common.token.repository.RefreshTokenRepository
import com.iksystem.common.user.model.User
import com.iksystem.common.user.repository.UserRepository
import com.iksystem.common.user.service.toResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

/**
 * Service handling two-phase authentication:
 * 1. **Login** — validates credentials, returns pre-auth token + membership list.
 * 2. **Select org** — accepts pre-auth token + org ID, returns full org-scoped JWT.
 *
 * Also handles registration (creates user identity), token refresh, and logout.
 */
@Service
class AuthService(
    private val userRepository: UserRepository,
    private val membershipRepository: MembershipRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val sessionRepository: SessionRepository,
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder,
    private val emailService: ResendService,
    @Value("\${jwt.access-token-expiration}") private val accessTokenExpiration: Long,
    @Value("\${jwt.refresh-token-expiration}") private val refreshTokenExpiration: Long,
) {

    /**
     * Registers a new user identity (no organization membership yet).
     * The user can then create an org or be invited to one.
     */
    @Transactional
    fun register(request: RegisterRequest): LoginResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw ConflictException("Email already registered")
        }

        val user = userRepository.save(
            User(
                email = request.email,
                password = passwordEncoder.encode(request.password),
                fullName = request.fullName,
                phoneNumber = request.phoneNumber,
            )
        )

        val preAuthToken = jwtService.generatePreAuthToken(user)

        emailService.sendRegistrationEmail(user.email, user.fullName)

        return LoginResponse(
            preAuthToken = preAuthToken,
            user = user.toResponse(),
            memberships = emptyList(),
        )
    }

    /**
     * Phase 1: Validates credentials and returns a pre-auth token with membership list.
     */
    @Transactional(readOnly = true)
    fun login(request: LoginRequest): LoginResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw UnauthorizedException("Invalid email or password")

        if (!user.active) {
            throw UnauthorizedException("Account is deactivated")
        }

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw UnauthorizedException("Invalid email or password")
        }

        val memberships = membershipRepository.findAllByUserId(user.id)
        val preAuthToken = jwtService.generatePreAuthToken(user)

        return LoginResponse(
            preAuthToken = preAuthToken,
            user = user.toResponse(),
            memberships = memberships.map { it.toSummary() },
        )
    }

    /**
     * Phase 2: Selects an organization and issues a full org-scoped JWT + refresh token.
     */
    @Transactional
    fun selectOrg(
        userId: Long,
        request: SelectOrgRequest,
        ipAddress: String?,
        userAgent: String?,
    ): AuthResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { NotFoundException("User not found") }

        if (!user.active) {
            throw UnauthorizedException("Account is deactivated")
        }

        val membership = membershipRepository.findByUserIdAndOrganizationId(userId, request.organizationId)
            ?: throw ForbiddenException("You are not a member of this organization")

        return createAuthResponse(user, membership, ipAddress, userAgent)
    }

    /**
     * Rotates a refresh token: revokes the old one and issues a new token pair.
     */
    @Transactional
    fun refresh(request: RefreshRequest): AuthResponse {
        val storedToken = refreshTokenRepository.findByToken(request.refreshToken)
            ?: throw UnauthorizedException("Invalid refresh token")

        if (storedToken.revoked || storedToken.expiresAt.isBefore(Instant.now())) {
            throw UnauthorizedException("Refresh token expired or revoked")
        }

        val user = storedToken.user
        if (!user.active) {
            throw UnauthorizedException("Account is deactivated")
        }

        val membership = membershipRepository.findByUserIdAndOrganizationId(user.id, storedToken.organizationId)
            ?: throw ForbiddenException("Membership no longer exists")

        // Revoke old refresh token (rotation)
        refreshTokenRepository.save(storedToken.copy(revoked = true))

        return createAuthResponse(user, membership, null, null)
    }

    /**
     * Returns all memberships for the given user.
     */
    @Transactional(readOnly = true)
    fun listMemberships(userId: Long): List<MembershipSummary> {
        return membershipRepository.findAllByUserId(userId).map { it.toSummary() }
    }

    /**
     * Switches the user's active organization. Issues a new JWT + refresh token
     * scoped to the target org and revokes tokens for the previous org.
     */
    @Transactional
    fun switchOrg(
        userId: Long,
        currentOrgId: Long,
        request: SelectOrgRequest,
        ipAddress: String?,
        userAgent: String?,
    ): AuthResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { NotFoundException("User not found") }

        if (!user.active) {
            throw UnauthorizedException("Account is deactivated")
        }

        val membership = membershipRepository.findByUserIdAndOrganizationId(userId, request.organizationId)
            ?: throw ForbiddenException("You are not a member of this organization")

        // Revoke tokens/sessions for the previous org
        refreshTokenRepository.revokeAllByUserIdAndOrganizationId(userId, currentOrgId)
        sessionRepository.deactivateAllByUserIdAndOrganizationId(userId, currentOrgId)

        return createAuthResponse(user, membership, ipAddress, userAgent)
    }

    /**
     * Logs out a user by revoking all their refresh tokens and deactivating all sessions.
     */
    @Transactional
    fun logout(userId: Long) {
        refreshTokenRepository.revokeAllByUserId(userId)
        sessionRepository.deactivateAllByUserId(userId)
    }

    private fun createAuthResponse(
        user: User,
        membership: Membership,
        ipAddress: String?,
        userAgent: String?,
    ): AuthResponse {
        val accessToken = jwtService.generateAccessToken(user, membership)
        val refreshTokenValue = UUID.randomUUID().toString()

        refreshTokenRepository.save(
            RefreshToken(
                token = refreshTokenValue,
                user = user,
                organizationId = membership.organization.id,
                expiresAt = Instant.now().plusMillis(refreshTokenExpiration),
            )
        )

        sessionRepository.save(
            Session(
                sessionId = UUID.randomUUID().toString(),
                user = user,
                organizationId = membership.organization.id,
                ipAddress = ipAddress,
                userAgent = userAgent,
                expiresAt = Instant.now().plusMillis(refreshTokenExpiration),
            )
        )

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshTokenValue,
            expiresIn = accessTokenExpiration / 1000,
            user = user.toResponse(),
            organizationId = membership.organization.id,
            role = membership.role.name,
        )
    }

    private fun Membership.toSummary() = MembershipSummary(
        membershipId = id,
        organizationId = organization.id,
        organizationName = organization.name,
        role = role.name,
    )
}
