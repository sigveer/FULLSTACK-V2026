package com.iksystem.`ik-common`.security

import com.iksystem.`ik-common`.membership.model.Membership
import com.iksystem.`ik-common`.organization.model.Organization
import com.iksystem.`ik-common`.user.model.Role
import com.iksystem.`ik-common`.user.model.User
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class JwtServiceTest : FunSpec({

    // 256-bit key for HMAC-SHA
    val secret = "test-secret-key-that-must-be-at-least-256-bits-long-for-hmac-sha!!"
    val jwtService = JwtService(secret = secret, accessTokenExpiration = 900_000L)

    val user = User(id = 1L, email = "test@example.com", password = "hashed", fullName = "Test User", phoneNumber = "+47123")
    val org = Organization(id = 1L, name = "Test Org")
    val membership = Membership(id = 1L, user = user, organization = org, role = Role.ADMIN)

    test("generatePreAuthToken creates valid token with pre_auth type") {
        val token = jwtService.generatePreAuthToken(user)

        val claims = jwtService.validateToken(token)
        claims.shouldNotBeNull()
        jwtService.getUserId(claims) shouldBe 1L
        jwtService.getTokenType(claims) shouldBe "pre_auth"
        jwtService.getRole(claims).shouldBeNull()
        jwtService.getOrganizationId(claims).shouldBeNull()
    }

    test("generateAccessToken creates valid token with role and orgId") {
        val token = jwtService.generateAccessToken(user, membership)

        val claims = jwtService.validateToken(token)
        claims.shouldNotBeNull()
        jwtService.getUserId(claims) shouldBe 1L
        jwtService.getTokenType(claims) shouldBe "access"
        jwtService.getRole(claims) shouldBe "ADMIN"
        jwtService.getOrganizationId(claims) shouldBe 1L
    }

    test("validateToken returns null for tampered token") {
        val token = jwtService.generateAccessToken(user, membership)

        val claims = jwtService.validateToken(token + "tampered")

        claims.shouldBeNull()
    }

    test("validateToken returns null for garbage input") {
        jwtService.validateToken("not.a.jwt").shouldBeNull()
    }

    test("validateToken returns null for token signed with different key") {
        val otherService = JwtService(
            secret = "different-secret-key-that-is-also-at-least-256-bits-long-for-hmac!!",
            accessTokenExpiration = 900_000L,
        )
        val token = otherService.generateAccessToken(user, membership)

        jwtService.validateToken(token).shouldBeNull()
    }
})
