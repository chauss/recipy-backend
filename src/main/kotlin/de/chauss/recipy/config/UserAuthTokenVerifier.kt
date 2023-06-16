package de.chauss.recipy.config

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.GrantedAuthority

interface UserAuthTokenVerifier : AuthenticationProvider {
    fun verifyToken(token: String): AuthenticatedUser

    class UserAuthenticationToken(
        authorities: Collection<GrantedAuthority?>?,
        private val jwt: String,
        user: AuthenticatedUser
    ) : AbstractAuthenticationToken(authorities) {
        private val user: AuthenticatedUser

        init {
            this.user = user
        }

        override fun getCredentials(): String {
            return jwt
        }

        override fun getPrincipal(): AuthenticatedUser {
            return user
        }
    }

    class AuthenticatedUser(
        val userId: String,
        val email: String,
        val name: String?,
        val claims: Map<String, Any>?,
        val issuer: String?,
        val picture: String?,
        val tenantId: String?,
        val emailVerified: Boolean?,
    )
}