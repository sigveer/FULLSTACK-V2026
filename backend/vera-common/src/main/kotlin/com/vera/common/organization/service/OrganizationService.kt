package com.iksystem.common.organization.service

import com.iksystem.common.exception.ConflictException
import com.iksystem.common.exception.NotFoundException
import com.iksystem.common.membership.model.Membership
import com.iksystem.common.membership.repository.MembershipRepository
import com.iksystem.common.organization.dto.CreateOrganizationRequest
import com.iksystem.common.organization.dto.OrganizationResponse
import com.iksystem.common.organization.model.Organization
import com.iksystem.common.organization.repository.OrganizationRepository
import com.iksystem.common.user.model.Role
import com.iksystem.common.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service layer for organization management.
 *
 * When a user creates an organization, they automatically become its ADMIN.
 */
@Service
class OrganizationService(
    private val organizationRepository: OrganizationRepository,
    private val membershipRepository: MembershipRepository,
    private val userRepository: UserRepository,
) {

    /**
     * Creates a new organization and makes the calling user its ADMIN.
     */
    @Transactional
    fun create(request: CreateOrganizationRequest, callerUserId: Long): OrganizationResponse {
        if (organizationRepository.existsByName(request.name)) {
            throw ConflictException("Organization name already exists")
        }

        val org = organizationRepository.save(
            Organization(name = request.name, orgNumber = request.orgNumber)
        )

        val user = userRepository.findById(callerUserId)
            .orElseThrow { NotFoundException("User not found") }

        membershipRepository.save(
            Membership(user = user, organization = org, role = Role.ADMIN)
        )

        return org.toResponse()
    }

    /**
     * Retrieves an organization by its primary key.
     *
     * @param id The organization ID.
     * @return The matching [OrganizationResponse].
     * @throws NotFoundException if no organization with the given [id] exists.
     */
    @Transactional(readOnly = true)
    fun getById(id: Long): OrganizationResponse {
        val org = organizationRepository.findById(id)
            .orElseThrow { NotFoundException("Organization not found") }
        return org.toResponse()
    }

    /**
     * Returns every organization in the system.
     *
     * @return A list of all [OrganizationResponse] entries.
     */
    @Transactional(readOnly = true)
    fun listAll(): List<OrganizationResponse> =
        organizationRepository.findAll().map { it.toResponse() }

    /**
     * Deletes an organization by its primary key.
     *
     * @param id The organization ID.
     * @throws NotFoundException if no organization with the given [id] exists.
     */
    @Transactional
    fun delete(id: Long) {
        if (!organizationRepository.existsById(id)) {
            throw NotFoundException("Organization not found")
        }
        organizationRepository.deleteById(id)
    }
}

/**
 * Extension function that maps an [Organization] entity to its API response DTO.
 */
fun Organization.toResponse() = OrganizationResponse(
    id = id,
    name = name,
    orgNumber = orgNumber,
)
