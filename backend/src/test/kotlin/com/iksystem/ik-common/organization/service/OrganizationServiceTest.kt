package com.iksystem.`ik-common`.organization.service

import com.ik.ikcommon.exception.ConflictException
import com.ik.ikcommon.exception.NotFoundException
import com.iksystem.`ik-common`.membership.model.Membership
import com.iksystem.`ik-common`.membership.repository.MembershipRepository
import com.iksystem.`ik-common`.organization.dto.CreateOrganizationRequest
import com.iksystem.`ik-common`.organization.model.Organization
import com.iksystem.`ik-common`.organization.repository.OrganizationRepository
import com.iksystem.`ik-common`.user.model.Role
import com.iksystem.`ik-common`.user.model.User
import com.iksystem.`ik-common`.user.repository.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.util.*

class OrganizationServiceTest : FunSpec({

    val organizationRepository = mockk<OrganizationRepository>()
    val membershipRepository = mockk<MembershipRepository>()
    val userRepository = mockk<UserRepository>()

    val service = OrganizationService(
        organizationRepository = organizationRepository,
        membershipRepository = membershipRepository,
        userRepository = userRepository,
    )

    val user = User(id = 1L, email = "test@example.com", password = "hashed", fullName = "Test User", phoneNumber = "+47123")
    val org = Organization(id = 1L, name = "Test Org", orgNumber = "123456")

    beforeTest {
        clearMocks(organizationRepository, membershipRepository, userRepository)
    }

    test("create saves org and makes caller ADMIN") {
        val request = CreateOrganizationRequest(name = "New Org", orgNumber = "999")
        every { organizationRepository.existsByName("New Org") } returns false
        every { organizationRepository.save(any()) } returns org.copy(name = "New Org")
        every { userRepository.findById(1L) } returns Optional.of(user)
        every { membershipRepository.save(any()) } answers { firstArg() }

        val result = service.create(request, 1L)

        result.name shouldBe "New Org"
        verify { membershipRepository.save(match { it.role == Role.ADMIN && it.user == user }) }
    }

    test("create throws ConflictException for duplicate name") {
        every { organizationRepository.existsByName("Test Org") } returns true

        shouldThrow<ConflictException> {
            service.create(CreateOrganizationRequest(name = "Test Org"), 1L)
        }
    }

    test("create throws NotFoundException for unknown user") {
        every { organizationRepository.existsByName("New Org") } returns false
        every { organizationRepository.save(any()) } returns org
        every { userRepository.findById(99L) } returns Optional.empty()

        shouldThrow<NotFoundException> {
            service.create(CreateOrganizationRequest(name = "New Org"), 99L)
        }
    }

    test("getById returns organization") {
        every { organizationRepository.findById(1L) } returns Optional.of(org)

        val result = service.getById(1L)

        result.id shouldBe 1L
        result.name shouldBe "Test Org"
        result.orgNumber shouldBe "123456"
    }

    test("getById throws NotFoundException for unknown id") {
        every { organizationRepository.findById(99L) } returns Optional.empty()

        shouldThrow<NotFoundException> {
            service.getById(99L)
        }
    }

    test("listAll returns all organizations") {
        val org2 = Organization(id = 2L, name = "Other Org")
        every { organizationRepository.findAll() } returns listOf(org, org2)

        val result = service.listAll()

        result shouldHaveSize 2
    }

    test("delete removes existing organization") {
        every { organizationRepository.existsById(1L) } returns true
        every { organizationRepository.deleteById(1L) } just runs

        service.delete(1L)

        verify { organizationRepository.deleteById(1L) }
    }

    test("delete throws NotFoundException for unknown id") {
        every { organizationRepository.existsById(99L) } returns false

        shouldThrow<NotFoundException> {
            service.delete(99L)
        }
    }
})
