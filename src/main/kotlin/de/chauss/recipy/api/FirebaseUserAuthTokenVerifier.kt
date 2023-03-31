package de.chauss.recipy.api

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import org.springframework.stereotype.Component

@Component
class FirebaseUserAuthTokenVerifier : UserAuthTokenVerifier {
    override fun verifyToken(token: String): String? {
        return try {
            val decodedToken = FirebaseAuth.getInstance().verifyIdToken(token)
            decodedToken.uid
        } catch (e: FirebaseAuthException) {
            null
        }
    }
}