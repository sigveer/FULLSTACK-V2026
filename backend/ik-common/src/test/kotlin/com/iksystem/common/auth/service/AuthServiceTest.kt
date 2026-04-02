package com.iksystem.common.auth.service

import com.iksystem.common.exception.ConflictException
import com.iksystem.common.exception.ForbiddenException
import com.iksystem.common.exception.NotFoundException
import com.iksystem.common.exception.UnauthorizedException
import com.iksystem.common.auth.dto.LoginRequest
import com.iksystem.common.auth.dto.RefreshRequest
import com.iksystem.common.auth.dto.RegisterRequest
import com.iksystem.common.auth.dto.SelectOrgRequest
import com.iksystem.common.membership.model.Membership
import com.iksystem.common.membership.repository.MembershipRepository
import com.iksystem.common.organization.model.Organization
import com.iksystem.common.security.JwtService
import com.iksystem.common.session.repository.SessionRepository
import com.iksystem.common.token.model.RefreshToken
import com.iksystem.common.token.repository.RefreshTokenRepository
import com.iksystem.common.user.model.Role
import com.iksystem.common.user.model.User
import com.iksystem.common.user.repository.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeBlank
import io.mockk.*
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Instant
import java.util.*

class AuthServiceTest : FunSpec({

    val userRepository = mockk<UserRepository>()
    val membershipRepository = mockk<MembershipRepository>()
    val refreshTokenRepository = mockk<RefreshTokenRepository>()
    val sessionRepository = mockk<SessionRepository>()
    val jwtService = mockk<JwtService>()
    val passwordEncoder = mockk<PasswordEncoder>()

    val service = AuthService(
        userRepository = userRepository,
        membershipRepository = membershipRepository,
        refreshTokenRepository = refreshTokenRepository,
        sessionRepository = sessionRepository,
        jwtService = jwtService,
        passwordEncoder = passwordEncoder,
        accessTokenExpiration = 900_000L,
        refreshTokenExpiration = 604_800_000L,
    )

    val org = Organization(id = 1L, name = "Test Org", orgNumber = "123")
    val user = User(id = 1L, email = "test@example.com", password = "hashed", fullName = "Test User", phoneNumber = "+47123")
    val membership = Membership(id = 1L, user = user, organization = org, role = Role.ADMIN)

    beforeTest {
        clearMocks(userRepository, membershipRepository, refreshTokenRepository, sessionRepository, jwtService, passwordEncoder)
    }

    test("register creates user and returns pre-auth token") {
        val request = RegisterRequest(email = "new@example.com", password = "password1", fullName = "New User", phoneNumber = "+47999")
        every { userRepository.existsByEmail("new@example.com") } returns false
        every { passwordEncoder.encode("password1") } returns "hashed"
        every { userRepository.save(any()) } answers { firstArg<User>().copy(id = 2L) }
        every { jwtService.generatePreAuthToken(any()) } returns "pre-auth-jwt"

        val result = service.register(request)

        result.preAuthToken shouldBe "pre-auth-jwt"
        result.user.email shouldBe "new@example.com"
        result.memberships.shouldBeEmpty()
    }

    test("register throws ConflictException for duplicate email") {
        val request = RegisterRequest(email = "existing@example.com", password = "password1", fullName = "User", phoneNumber = "+47000")
        every { userRepository.existsByEmail("existing@example.com") } returns true

        shouldThrow<ConflictException> {
            service.register(request)
        }.message shouldBe "Email already registered"
    }

    test("login returns pre-auth token and memberships") {
        val request = LoginRequest(email = "test@example.com", password = "password1")
        every { userRepository.findByEmail("test@example.com") } returns user
        every { passwordEncoder.matches("password1", "hashed") } returns true
        every { membershipRepository.findAllByUserId(1L) } returns listOf(membership)
        every { jwtService.generatePreAuthToken(user) } returns "pre-auth-jwt"

        val result = service.login(request)

        result.preAuthToken shouldBe "pre-auth-jwt"
        result.memberships shouldHaveSize 1
        result.memberships[0].organizationName shouldBe "Test Org"
    }

    test("login throws for unknown email") {
        every { userRepository.findByEmail("unknown@example.com") } returns null

        shouldThrow<UnauthorizedException> {
            service.login(LoginRequest("unknown@example.com", "pass"))
        }
    }

    test("login throws for wrong password") {
        every { userRepository.findByEmail("test@example.com") } returns user
        every { passwordEncoder.matches("wrong", "hashed") } returns false

        shouldThrow<UnauthorizedException> {
            service.login(LoginRequest("test@example.com", "wrong"))
        }
    }

    test("login throws for deactivated account") {
        val inactive = user.copy(active = false)
        every { userRepository.findByEmail("test@example.com") } returns inactive

        shouldThrow<UnauthorizedException> {
            service.login(LoginRequest("test@example.com", "password1"))
        }.message shouldBe "Account is deactivated"
    }

    test("selectOrg returns full auth response") {
        every { userRepository.findById(1L) } returns Optional.of(user)
        every { membershipRepository.findByUserIdAndOrganizationId(1L, 1L) } returns membership
        every { jwtService.generateAccessToken(user, membership) } returns "access-jwt"
        every { refreshTokenRepository.save(any()) } answers { firstArg() }
        every { sessionRepository.save(any()) } answers { firstArg() }

        val result = service.selectOrg(1L, SelectOrgRequest(1L), "127.0.0.1", "Mozilla")

        result.accessToken shouldBe "access-jwt"
        result.refreshToken.shouldNotBeBlank()
        result.role shouldBe "ADMIN"
        result.organizationId shouldBe 1L
    }

    test("selectOrg throws for non-member") {
        every { userRepository.findById(1L) } returns Optional.of(user)
        every { membershipRepository.findByUserIdAndOrganizationId(1L, 99L) } returns null

        shouldThrow<ForbiddenException> {
            service.selectOrg(1L, SelectOrgRequest(99L), null, null)
        }
    }

    test("selectOrg throws for deactivated user") {
        val inactive = user.copy(active = false)
        every { userRepository.findById(1L) } returns Optional.of(inactive)

        shouldThrow<UnauthorizedException> {
            service.selectOrg(1L, SelectOrgRequest(1L), null, null)
        }
    }

    test("refresh rotates token and returns new auth response") {
        val storedToken = RefreshToken(
            id = 1L, token = "old-refresh", user = user,
            organizationId = 1L, expiresAt = Instant.now().plusSeconds(3600),
        )
        every { refreshTokenRepository.findByToken("old-refresh") } returns storedToken
        every { membershipRepository.findByUserIdAndOrganizationId(1L, 1L) } returns membership
        every { refreshTokenRepository.save(any()) } answers { firstArg() }
        every { sessionRepository.save(any()) } answers { firstArg() }
        every { jwtService.generateAccessToken(user, membership) } returns "new-access-jwt"

        val result = service.refresh(RefreshRequest("old-refresh"))

        result.accessToken shouldBe "new-access-jwt"
        verify { refreshTokenRepository.save(match { it.revoked }) }
    }

    test("refresh throws for invalid token") {
        every { refreshTokenRepository.findByToken("bad-token") } returns null

        shouldThrow<UnauthorizedException> {
            service.refresh(RefreshRequest("bad-token"))
        }
    }

    test("refresh throws for expired token") {
        val expired = RefreshToken(
            id = 1L, token = "expired", user = user,
            organizationId = 1L, expiresAt = Instant.now().minusSeconds(3600),
        )
        every { refreshTokenRepository.findByToken("expired") } returns expired

        shouldThrow<UnauthorizedException> {
            service.refresh(RefreshRequest("expired"))
        }
    }

    test("refresh throws for revoked token") {
        val revoked = RefreshToken(
            id = 1L, token = "revoked", user = user,
            organizationId = 1L, expiresAt = Instant.now().plusSeconds(3600), revoked = true,
        )
        every { refreshTokenRepository.findByToken("revoked") } returns revoked

        shouldThrow<UnauthorizedException> {
            service.refresh(RefreshRequest("revoked"))
        }
    }

    test("switchOrg revokes old org tokens and issues new ones") {
        every { userRepository.findById(1L) } returns Optional.of(user)
        every { membershipRepository.findByUserIdAndOrganizationId(1L, 2L) } returns membership.copy(id = 2L)
        every { refreshTokenRepository.revokeAllByUserIdAndOrganizationId(1L, 1L) } just runs
        every { sessionRepository.deactivateAllByUserIdAndOrganizationId(1L, 1L) } just runs
        every { jwtService.generateAccessToken(any(), any()) } returns "new-org-jwt"
        every { refreshTokenRepository.save(any()) } answers { firstArg() }
        every { sessionRepository.save(any()) } answers { firstArg() }

        val result = service.switchOrg(1L, 1L, SelectOrgRequest(2L), null, null)

        result.accessToken shouldBe "new-org-jwt"
        verify { refreshTokenRepository.revokeAllByUserIdAndOrganizationId(1L, 1L) }
        verify { sessionRepository.deactivateAllByUserIdAndOrganizationId(1L, 1L) }
    }

    test("logout revokes all tokens and sessions") {
        every { refreshTokenRepository.revokeAllByUserId(1L) } just runs
        every { sessionRepository.deactivateAllByUserId(1L) } just runs

        service.logout(1L)

        verify { refreshTokenRepository.revokeAllByUserId(1L) }
        verify { sessionRepository.deactivateAllByUserId(1L) }
    }

    test("listMemberships returns summaries") {
        every { membershipRepository.findAllByUserId(1L) } returns listOf(membership)

        val result = service.listMemberships(1L)

        result shouldHaveSize 1
        result[0].organizationName shouldBe "Test Org"
        result[0].role shouldBe "ADMIN"
    }
})
