package de.chauss.recipy.config

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import de.chauss.recipy.config.UserAuthTokenVerifier.AuthenticatedUser
import de.chauss.recipy.config.UserAuthTokenVerifier.UserAuthenticationToken
import mu.KotlinLogging
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken
import org.springframework.stereotype.Component

@Component
class FirebaseUserAuthTokenVerifier : UserAuthTokenVerifier {
    private val logger = KotlinLogging.logger {}

    override fun verifyToken(token: String): AuthenticatedUser {
        val decodedToken = FirebaseAuth.getInstance().verifyIdToken(token, true)
        return AuthenticatedUser(
            decodedToken.uid,
            decodedToken.email,
            decodedToken.name,
            decodedToken.claims,
            decodedToken.issuer,
            decodedToken.picture,
            decodedToken.tenantId,
            decodedToken.isEmailVerified,
        )
    }

    override fun authenticate(authentication: Authentication?): Authentication {
        val token = (authentication as BearerTokenAuthenticationToken).token

        val authenticatedUser: AuthenticatedUser
        try {
            authenticatedUser = verifyToken(token)
        } catch (e: FirebaseAuthException) {
            logger.debug { e.message }
            throw BadCredentialsException("Failed to verify userToken", e)
        }

        val authenticationResult =
            UserAuthenticationToken(authentication.authorities, token, authenticatedUser)
        authenticationResult.isAuthenticated = true

        return authenticationResult
    }

    override fun supports(authentication: Class<*>): Boolean {
        val assignableFrom =
            BearerTokenAuthenticationToken::class.java.isAssignableFrom(authentication)
        return assignableFrom
    }
}