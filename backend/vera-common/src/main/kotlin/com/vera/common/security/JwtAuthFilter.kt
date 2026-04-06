package com.iksystem.common.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Servlet filter that extracts the JWT from the `Authorization: Bearer <token>` header,
 * validates it, and populates the Spring Security context.
 *
 * Handles both pre-auth tokens (no role/orgId) and full access tokens.
 * Pre-auth tokens get an empty authority list — they can only hit select-org.
 */
@Component
class JwtAuthFilter(private val jwtService: JwtService) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (header != null && header.startsWith("Bearer ")) {
            val token = header.substring(7)
            val claims = jwtService.validateToken(token)

            if (claims != null) {
                val userId = jwtService.getUserId(claims)
                val tokenType = jwtService.getTokenType(claims)
                val role = jwtService.getRole(claims)
                val orgId = jwtService.getOrganizationId(claims)

                val authorities = if (tokenType == "access" && role != null) {
                    listOf(SimpleGrantedAuthority("ROLE_$role"))
                } else {
                    emptyList()
                }

                val principal = AuthenticatedUser(userId, orgId, role)
                val auth = UsernamePasswordAuthenticationToken(principal, null, authorities)
                SecurityContextHolder.getContext().authentication = auth
            }
        }

        filterChain.doFilter(request, response)
    }
}
