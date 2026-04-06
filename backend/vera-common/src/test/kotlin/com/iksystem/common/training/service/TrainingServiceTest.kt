package com.iksystem.common.training.service

import com.iksystem.common.exception.BadRequestException
import com.iksystem.common.exception.NotFoundException
import com.iksystem.common.membership.repository.MembershipRepository
import com.iksystem.common.security.AuthenticatedUser
import com.iksystem.common.training.dto.CreateTrainingLogRequest
import com.iksystem.common.training.dto.UpdateTrainingLogRequest
import com.iksystem.common.training.model.TrainingLog
import com.iksystem.common.training.model.TrainingStatus
import com.iksystem.common.training.repository.TrainingRepository
import com.iksystem.common.user.model.User
import com.iksystem.common.user.repository.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.time.Instant
import java.util.*

class TrainingServiceTest : FunSpec({

    val trainingRepository = mockk<TrainingRepository>()
    val userRepository = mockk<UserRepository>()
    val membershipRepository = mockk<MembershipRepository>()

    val service = TrainingService(
        trainingRepository = trainingRepository,
        userRepository = userRepository,
        membershipRepository = membershipRepository,
    )

    val admin = User(id = 1L, email = "admin@test.com", password = "hashed", fullName = "Admin User", phoneNumber = "+47123")
    val employee = User(id = 2L, email = "employee@test.com", password = "hashed", fullName = "Employee User", phoneNumber = "+47124")
    val orgId = 10L
    val auth = AuthenticatedUser(userId = 1L, organizationId = orgId, role = "ADMIN")

    val trainingLog = TrainingLog(
        id = 1L,
        organizationId = orgId,
        employeeUser = employee,
        loggedByUser = admin,
        title = "Brannvernkurs",
        description = "Årlig brannvernopplæring",
        completedAt = Instant.parse("2026-01-15T10:00:00Z"),
        expiresAt = Instant.parse("2027-01-15T10:00:00Z"),
        status = TrainingStatus.COMPLETED,
    )

    beforeTest {
        clearMocks(trainingRepository, userRepository, membershipRepository)
    }

    test("list returns all training logs for the organization") {
        val log2 = trainingLog.copy(id = 2L, title = "HMS-kurs")
        every { trainingRepository.findAllByOrganizationIdOrderByCreatedAtDesc(orgId) } returns listOf(trainingLog, log2)

        val result = service.list(auth)

        result shouldHaveSize 2
        result[0].title shouldBe "Brannvernkurs"
        result[1].title shouldBe "HMS-kurs"
    }

    test("getById returns training log") {
        every { trainingRepository.findByIdAndOrganizationId(1L, orgId) } returns trainingLog

        val result = service.getById(1L, auth)

        result.id shouldBe 1L
        result.title shouldBe "Brannvernkurs"
        result.employeeUserId shouldBe 2L
        result.employeeUserName shouldBe "Employee User"
        result.loggedByUserId shouldBe 1L
        result.loggedByUserName shouldBe "Admin User"
        result.status shouldBe TrainingStatus.COMPLETED
    }

    test("getById throws NotFoundException for unknown id") {
        every { trainingRepository.findByIdAndOrganizationId(99L, orgId) } returns null

        shouldThrow<NotFoundException> {
            service.getById(99L, auth)
        }
    }

    test("create saves training log and sets loggedByUser from auth") {
        val request = CreateTrainingLogRequest(
            employeeUserId = 2L,
            title = "Brannvernkurs",
            description = "Årlig brannvernopplæring",
            completedAt = "2026-01-15T10:00:00Z",
            expiresAt = "2027-01-15T10:00:00Z",
            status = TrainingStatus.COMPLETED,
        )

        every { userRepository.findById(2L) } returns Optional.of(employee)
        every { membershipRepository.existsByUserIdAndOrganizationId(2L, orgId) } returns true
        every { userRepository.findById(1L) } returns Optional.of(admin)
        every { trainingRepository.save(any()) } answers { firstArg<TrainingLog>().copy(id = 1L) }

        val result = service.create(request, auth)

        result.title shouldBe "Brannvernkurs"
        result.employeeUserId shouldBe 2L
        result.loggedByUserId shouldBe 1L
        result.status shouldBe TrainingStatus.COMPLETED
        verify { trainingRepository.save(match { it.loggedByUser == admin && it.employeeUser == employee }) }
    }

    test("create throws BadRequestException when employee is not a member of the org") {
        val request = CreateTrainingLogRequest(
            employeeUserId = 2L,
            title = "Kurs",
            status = TrainingStatus.NOT_COMPLETED,
        )

        every { userRepository.findById(2L) } returns Optional.of(employee)
        every { membershipRepository.existsByUserIdAndOrganizationId(2L, orgId) } returns false

        shouldThrow<BadRequestException> {
            service.create(request, auth)
        }
    }

    test("create throws NotFoundException for unknown employee") {
        val request = CreateTrainingLogRequest(
            employeeUserId = 99L,
            title = "Kurs",
            status = TrainingStatus.NOT_COMPLETED,
        )

        every { userRepository.findById(99L) } returns Optional.empty()

        shouldThrow<NotFoundException> {
            service.create(request, auth)
        }
    }

    test("update modifies training log fields") {
        val request = UpdateTrainingLogRequest(
            title = "Updated Title",
            status = TrainingStatus.EXPIRED,
        )

        every { trainingRepository.findByIdAndOrganizationId(1L, orgId) } returns trainingLog
        every { trainingRepository.save(any()) } answers { firstArg() }

        val result = service.update(1L, request, auth)

        result.title shouldBe "Updated Title"
        result.status shouldBe TrainingStatus.EXPIRED
        result.employeeUserId shouldBe 2L
    }

    test("update changes employee when employeeUserId is provided") {
        val newEmployee = User(id = 3L, email = "new@test.com", password = "hashed", fullName = "New Employee", phoneNumber = "+47125")
        val request = UpdateTrainingLogRequest(employeeUserId = 3L)

        every { trainingRepository.findByIdAndOrganizationId(1L, orgId) } returns trainingLog
        every { userRepository.findById(3L) } returns Optional.of(newEmployee)
        every { membershipRepository.existsByUserIdAndOrganizationId(3L, orgId) } returns true
        every { trainingRepository.save(any()) } answers { firstArg() }

        val result = service.update(1L, request, auth)

        result.employeeUserId shouldBe 3L
        result.employeeUserName shouldBe "New Employee"
    }

    test("update throws BadRequestException when no fields provided") {
        val request = UpdateTrainingLogRequest()

        every { trainingRepository.findByIdAndOrganizationId(1L, orgId) } returns trainingLog

        shouldThrow<BadRequestException> {
            service.update(1L, request, auth)
        }
    }

    test("update throws NotFoundException for unknown training log") {
        val request = UpdateTrainingLogRequest(title = "New Title")

        every { trainingRepository.findByIdAndOrganizationId(99L, orgId) } returns null

        shouldThrow<NotFoundException> {
            service.update(99L, request, auth)
        }
    }

    test("delete removes existing training log") {
        every { trainingRepository.findByIdAndOrganizationId(1L, orgId) } returns trainingLog
        every { trainingRepository.delete(trainingLog) } just runs

        service.delete(1L, auth)

        verify { trainingRepository.delete(trainingLog) }
    }

    test("delete throws NotFoundException for unknown training log") {
        every { trainingRepository.findByIdAndOrganizationId(99L, orgId) } returns null

        shouldThrow<NotFoundException> {
            service.delete(99L, auth)
        }
    }
})
