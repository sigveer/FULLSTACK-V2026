package com.iksystem.common.invite.service

import com.iksystem.common.auth.dto.AuthResponse
import com.iksystem.common.exception.BadRequestException
import com.iksystem.common.invite.dto.InviteDetailsResponse
import com.iksystem.common.invite.dto.InviteUserRequest
import com.iksystem.common.invite.model.Invitation
import com.iksystem.common.invite.repository.InvitationRepository
import com.iksystem.common.membership.model.Membership
import com.iksystem.common.membership.repository.MembershipRepository
import com.iksystem.common.organization.repository.OrganizationRepository
import com.iksystem.common.resend.service.ResendService
import com.iksystem.common.security.AuthenticatedUser
import com.iksystem.common.security.JwtService
import com.iksystem.common.session.model.Session
import com.iksystem.common.session.repository.SessionRepository
import com.iksystem.common.token.model.RefreshToken
import com.iksystem.common.token.repository.RefreshTokenRepository
import com.iksystem.common.user.model.Role
import com.iksystem.common.user.model.User
import com.iksystem.common.user.repository.UserRepository
import com.iksystem.common.user.service.toResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
class InvitationService(
    private val invitationRepository: InvitationRepository,
    private val userRepository: UserRepository,
    private val membershipRepository: MembershipRepository,
    private val organizationRepository: OrganizationRepository,
    private val resendService: ResendService,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val sessionRepository: SessionRepository,
    @Value("\${jwt.access-token-expiration}") private val accessTokenExpiration: Long,
    @Value("\${jwt.refresh-token-expiration}") private val refreshTokenExpiration: Long,
) {

    @Transactional
    fun createInvite(request: InviteUserRequest, auth: AuthenticatedUser) {
        val orgId = auth.requireOrganizationId()
        val email = request.email.lowercase().trim()

        val existingUser = userRepository.findByEmail(email)
        if (existingUser != null) {
            if (membershipRepository.existsByUserIdAndOrganizationId(existingUser.id, orgId)) {
                throw BadRequestException("Brukeren er allerede medlem av denne organisasjonen")
            }
        }

        if (invitationRepository.existsByEmailAndOrganizationIdAndAcceptedAtIsNull(email, orgId)) {
            throw BadRequestException("En invitasjon er allerede sendt til denne e-postadressen")
        }

        val token = UUID.randomUUID().toString()
        invitationRepository.save(
            Invitation(
                organizationId = orgId,
                email = email,
                role = request.role,
                token = token,
                invitedById = auth.userId,
                expiresAt = Instant.now().plus(7, ChronoUnit.DAYS),
            )
        )

        val org = organizationRepository.findById(orgId).get()
        resendService.sendInviteEmail(email, org.name, token)
    }

    @Transactional(readOnly = true)
    fun getValidInvite(token: String): InviteDetailsResponse {
        val invite = invitationRepository.findByToken(token)
            ?: throw BadRequestException("Invitasjonen er ugyldig")

        if (invite.expiresAt.isBefore(Instant.now()) || invite.acceptedAt != null) {
            throw BadRequestException("Invitasjonen er ikke lenger gyldig")
        }

        val org = organizationRepository.findById(invite.organizationId).get()
        val existingUser = userRepository.findByEmail(invite.email) != null

        return InviteDetailsResponse(
            email = invite.email,
            organizationName = org.name,
            existingUser = existingUser,
        )
    }

    @Transactional
    fun acceptInvitation(token: String, password: String?, fullName: String?, phoneNumber: String?): AuthResponse {
        val invite = invitationRepository.findByToken(token)
            ?: throw BadRequestException("Invitasjonen er ugyldig")

        if (invite.expiresAt.isBefore(Instant.now()) || invite.acceptedAt != null) {
            throw BadRequestException("Invitasjonen er ikke lenger gyldig")
        }

        var user = userRepository.findByEmail(invite.email)

        if (user == null) {
            if (password == null || fullName == null || phoneNumber == null) {
                throw BadRequestException("Passord, navn og telefonnummer er påkrevd for nye brukere")
            }
            user = userRepository.save(
                User(
                    email = invite.email,
                    password = passwordEncoder.encode(password),
                    fullName = fullName,
                    phoneNumber = phoneNumber,
                    active = true,
                )
            )
        } else {
            if (password == null || !passwordEncoder.matches(password, user.password)) {
                throw BadRequestException("Feil passord")
            }
        }

        val org = organizationRepository.findById(invite.organizationId).get()
        val membership = membershipRepository.save(
            Membership(
                user = user,
                organization = org,
                role = Role.valueOf(invite.role),
            )
        )

        invite.acceptedAt = Instant.now()
        invitationRepository.save(invite)

        val accessToken = jwtService.generateAccessToken(user, membership)
        val refreshTokenValue = UUID.randomUUID().toString()

        refreshTokenRepository.save(
            RefreshToken(
                token = refreshTokenValue,
                user = user,
                organizationId = org.id,
                expiresAt = Instant.now().plusMillis(refreshTokenExpiration),
            )
        )

        sessionRepository.save(
            Session(
                sessionId = UUID.randomUUID().toString(),
                user = user,
                organizationId = org.id,
                ipAddress = null,
                userAgent = null,
                expiresAt = Instant.now().plusMillis(refreshTokenExpiration),
            )
        )

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshTokenValue,
            expiresIn = accessTokenExpiration / 1000,
            user = user.toResponse(),
            organizationId = org.id,
            role = membership.role.name,
        )
    }
}
