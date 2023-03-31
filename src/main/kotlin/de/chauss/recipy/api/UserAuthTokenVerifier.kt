package de.chauss.recipy.api

interface UserAuthTokenVerifier {
    fun verifyToken(token: String): String?
}