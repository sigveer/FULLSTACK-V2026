package com.iksystem.common.user.service

import com.iksystem.common.exception.BadRequestException
import com.iksystem.common.exception.ConflictException
import com.iksystem.common.exception.ForbiddenException
import com.iksystem.common.exception.NotFoundException
import com.iksystem.common.membership.model.Membership
import com.iksystem.common.membership.repository.MembershipRepository
import com.iksystem.common.organization.model.Organization
import com.iksystem.common.organization.repository.OrganizationRepository
import com.iksystem.common.security.AuthenticatedUser
import com.iksystem.common.session.repository.SessionRepository
import com.iksystem.common.token.repository.RefreshTokenRepository
import com.iksystem.common.user.dto.CreateUserRequest
import com.iksystem.common.user.dto.UpdateUserRoleRequest
import com.iksystem.common.user.model.Role
import com.iksystem.common.user.model.User
import com.iksystem.common.user.repository.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

class UserServiceTest : FunSpec({

    val userRepository = mockk<UserRepository>()
    val membershipRepository = mockk<MembershipRepository>()
    val organizationRepository = mockk<OrganizationRepository>()
    val refreshTokenRepository = mockk<RefreshTokenRepository>()
    val sessionRepository = mockk<SessionRepository>()
    val passwordEncoder = mockk<PasswordEncoder>()

    val service = UserService(
        userRepository = userRepository,
        membershipRepository = membershipRepository,
        organizationRepository = organizationRepository,
        refreshTokenRepository = refreshTokenRepository,
        sessionRepository = sessionRepository,
        passwordEncoder = passwordEncoder,
    )

    val org = Organization(id = 1L, name = "Test Org", orgNumber = "123")
    val user = User(id = 1L, email = "test@example.com", password = "hashed", fullName = "Test User", phoneNumber = "+47123")
    val otherUser = User(id = 2L, email = "other@example.com", password = "hashed", fullName = "Other User", phoneNumber = "+47456")
    val membership = Membership(id = 1L, user = user, organization = org, role = Role.ADMIN)
    val otherMembership = Membership(id = 2L, user = otherUser, organization = org, role = Role.EMPLOYEE)
    val auth = AuthenticatedUser(userId = 1L, organizationId = 1L, role = "ADMIN")

    beforeTest {
        clearMocks(userRepository, membershipRepository, organizationRepository, refreshTokenRepository, sessionRepository, passwordEncoder)
    }

    test("listUsers returns all memberships in org") {
        every { membershipRepository.findAllByOrganizationId(1L) } returns listOf(membership, otherMembership)

        val result = service.listUsers(auth)

        result shouldHaveSize 2
        result[0].userEmail shouldBe "test@example.com"
        result[1].role shouldBe "EMPLOYEE"
    }

    test("getUser returns membership for user in org") {
        every { membershipRepository.findByUserIdAndOrganizationId(2L, 1L) } returns otherMembership

        val result = service.getUser(2L, auth)

        result.userId shouldBe 2L
        result.role shouldBe "EMPLOYEE"
    }

    test("getUser throws NotFoundException when user not in org") {
        every { membershipRepository.findByUserIdAndOrganizationId(99L, 1L) } returns null

        shouldThrow<NotFoundException> {
            service.getUser(99L, auth)
        }
    }

    test("getCurrentUser returns caller profile") {
        every { userRepository.findById(1L) } returns Optional.of(user)

        val result = service.getCurrentUser(auth)

        result.email shouldBe "test@example.com"
        result.fullName shouldBe "Test User"
    }

    test("createUser creates new user and membership") {
        val request = CreateUserRequest(
            email = "new@example.com", password = "password1",
            fullName = "New User", phoneNumber = "+47789", role = "EMPLOYEE",
        )
        every { organizationRepository.findById(1L) } returns Optional.of(org)
        every { userRepository.findByEmail("new@example.com") } returns null
        every { passwordEncoder.encode("password1") } returns "hashed"
        every { userRepository.save(any()) } answers { firstArg<User>().copy(id = 3L) }
        every { membershipRepository.save(any()) } answers { firstArg<Membership>().copy(id = 3L) }

        val result = service.createUser(request, auth)

        result.role shouldBe "EMPLOYEE"
        verify { userRepository.save(any()) }
        verify { membershipRepository.save(any()) }
    }

    test("createUser adds existing user to org") {
        val request = CreateUserRequest(
            email = "other@example.com", password = "password1",
            fullName = "Other User", phoneNumber = "+47456", role = "MANAGER",
        )
        every { organizationRepository.findById(1L) } returns Optional.of(org)
        every { userRepository.findByEmail("other@example.com") } returns otherUser
        every { membershipRepository.existsByUserIdAndOrganizationId(2L, 1L) } returns false
        every { membershipRepository.save(any()) } answers { firstArg<Membership>().copy(id = 5L) }

        val result = service.createUser(request, auth)

        result.role shouldBe "MANAGER"
        verify(exactly = 0) { userRepository.save(any()) }
    }

    test("createUser throws ConflictException if user already in org") {
        val request = CreateUserRequest(
            email = "other@example.com", password = "password1",
            fullName = "Other User", phoneNumber = "+47456", role = "EMPLOYEE",
        )
        every { organizationRepository.findById(1L) } returns Optional.of(org)
        every { userRepository.findByEmail("other@example.com") } returns otherUser
        every { membershipRepository.existsByUserIdAndOrganizationId(2L, 1L) } returns true

        shouldThrow<ConflictException> {
            service.createUser(request, auth)
        }
    }

    test("createUser throws BadRequestException for invalid role") {
        val request = CreateUserRequest(
            email = "new@example.com", password = "password1",
            fullName = "New", phoneNumber = "+47000", role = "SUPERADMIN",
        )
        every { organizationRepository.findById(1L) } returns Optional.of(org)

        shouldThrow<BadRequestException> {
            service.createUser(request, auth)
        }
    }

    test("updateUserRole changes role") {
        every { membershipRepository.findByUserIdAndOrganizationId(2L, 1L) } returns otherMembership
        every { membershipRepository.save(any()) } answers { firstArg() }

        val result = service.updateUserRole(2L, UpdateUserRoleRequest("MANAGER"), auth)

        result.role shouldBe "MANAGER"
    }

    test("deactivateUser sets active to false and revokes tokens") {
        every { membershipRepository.findByUserIdAndOrganizationId(2L, 1L) } returns otherMembership
        every { userRepository.findById(2L) } returns Optional.of(otherUser)
        every { refreshTokenRepository.revokeAllByUserId(2L) } just runs
        every { sessionRepository.deactivateAllByUserId(2L) } just runs
        every { userRepository.save(any()) } answers { firstArg() }

        val result = service.deactivateUser(2L, auth)

        result.active shouldBe false
        verify { refreshTokenRepository.revokeAllByUserId(2L) }
    }

    test("deactivateUser throws ForbiddenException for self-deactivation") {
        every { membershipRepository.findByUserIdAndOrganizationId(1L, 1L) } returns membership

        shouldThrow<ForbiddenException> {
            service.deactivateUser(1L, auth)
        }
    }

    test("activateUser sets active to true") {
        val inactive = otherUser.copy(active = false)
        every { membershipRepository.findByUserIdAndOrganizationId(2L, 1L) } returns otherMembership
        every { userRepository.findById(2L) } returns Optional.of(inactive)
        every { userRepository.save(any()) } answers { firstArg() }

        val result = service.activateUser(2L, auth)

        result.active shouldBe true
    }

    test("kickUser revokes sessions and tokens") {
        every { membershipRepository.findByUserIdAndOrganizationId(2L, 1L) } returns otherMembership
        every { refreshTokenRepository.revokeAllByUserId(2L) } just runs
        every { sessionRepository.deactivateAllByUserId(2L) } just runs

        service.kickUser(2L, auth)

        verify { refreshTokenRepository.revokeAllByUserId(2L) }
        verify { sessionRepository.deactivateAllByUserId(2L) }
    }

    test("kickUser throws ForbiddenException for self-kick") {
        every { membershipRepository.findByUserIdAndOrganizationId(1L, 1L) } returns membership

        shouldThrow<ForbiddenException> {
            service.kickUser(1L, auth)
        }
    }

    test("removeMember deletes membership and revokes org tokens") {
        every { membershipRepository.findByUserIdAndOrganizationId(2L, 1L) } returns otherMembership
        every { membershipRepository.delete(otherMembership) } just runs
        every { refreshTokenRepository.revokeAllByUserIdAndOrganizationId(2L, 1L) } just runs
        every { sessionRepository.deactivateAllByUserIdAndOrganizationId(2L, 1L) } just runs

        service.removeMember(2L, auth)

        verify { membershipRepository.delete(otherMembership) }
        verify { refreshTokenRepository.revokeAllByUserIdAndOrganizationId(2L, 1L) }
    }

    test("removeMember throws ForbiddenException for self-removal") {
        every { membershipRepository.findByUserIdAndOrganizationId(1L, 1L) } returns membership

        shouldThrow<ForbiddenException> {
            service.removeMember(1L, auth)
        }
    }
})
