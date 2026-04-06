package com.iksystem.common.invite.repository

import com.iksystem.common.invite.model.Invitation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface InvitationRepository : JpaRepository<Invitation, Long> {
    fun findByToken(token: String): Invitation?
    fun findAllByOrganizationId(organizationId: Long): List<Invitation>
    fun existsByEmailAndOrganizationIdAndAcceptedAtIsNull(email: String, organizationId: Long): Boolean
}
