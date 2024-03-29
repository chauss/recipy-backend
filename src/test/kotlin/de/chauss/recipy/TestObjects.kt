package de.chauss.recipy

import de.chauss.recipy.config.UserAuthTokenVerifier

class TestObjects {
    companion object {
        const val TEST_USER_ID = "test-user-id"
        const val VALID_BUT_EXPIRED_TOKEN =
            "eyJhbGciOiJSUzI1NiIsImtpZCI6IjY3YmFiYWFiYTEwNWFkZDZiM2ZiYjlmZjNmZjVmZTNkY2E0Y2VkYTEiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vcmVjaXB5LTliOGFkIiwiYXVkIjoicmVjaXB5LTliOGFkIiwiYXV0aF90aW1lIjoxNjg2OTIwNjk1LCJ1c2VyX2lkIjoieUY2RFFHRnYyNk9vd2V5dVFXNDdpQjd5UDdGMiIsInN1YiI6InlGNkRRR0Z2MjZPb3dleXVRVzQ3aUI3eVA3RjIiLCJpYXQiOjE2ODY5MjA2OTUsImV4cCI6MTY4NjkyNDI5NSwiZW1haWwiOiJ0ZXN0QHRlc3QuZGUiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsImZpcmViYXNlIjp7ImlkZW50aXRpZXMiOnsiZW1haWwiOlsidGVzdEB0ZXN0LmRlIl19LCJzaWduX2luX3Byb3ZpZGVyIjoicGFzc3dvcmQifX0.CtzTvoQwBn5N7PFpXtFMqvM1xSt1DNgrtKWqjbP8spH4h_DSszURfUYreQwekg2YaFeupQRS9puEoj_S4aHyb5upkOu5dYHUUAHIhTDvlM7FrHmjo9yEqVKz1r_pjAlPPmR4b6byiuvthsecKN4NEMe_urtHoxNite_JSjyUH2suuU7fhHZ8UgKFAYlpVKaiJSsZ19uVfXKY4WrfQbSvnwJmCyQY-uH4a4z4Yay0jDNA-cW9zdLZTrpL6Nh0Jv9MeVXBqxlIqmT7cfTS6IUOIox1-NkU5PnQ_ZLZcY0m38nsN5179JZJMMcM-cEVaXOTuJQY82z5fi7a_KXGHEWsEA"

        val authenticatedTestUser = UserAuthTokenVerifier.AuthenticatedUser(
            userId = TEST_USER_ID,
            email = "fake-email",
            name = "fake-name",
            claims = hashMapOf("fake-claim" to "some claim"),
            issuer = "fake-issuer",
            picture = "fake-picture",
            tenantId = "fake-tenantId",
            emailVerified = true,
        )
    }
}