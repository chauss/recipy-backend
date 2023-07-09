package de.chauss.recipy.api

import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.google.firebase.ErrorCode
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthException
import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import de.chauss.recipy.config.FirebaseConfig
import de.chauss.recipy.config.FirebaseUserAuthTokenVerifier
import de.chauss.recipy.service.ActionResult
import de.chauss.recipy.service.ActionResultStatus
import de.chauss.recipy.service.RecipeService
import de.chauss.recipy.service.dtos.RecipeDto
import io.mockk.every
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RecipeRestControllerAuthITest(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    lateinit var firebaseConfig: FirebaseConfig

    @SpykBean
    lateinit var firebaseUserAuthTokenVerifier: FirebaseUserAuthTokenVerifier

    @MockkBean
    lateinit var recipeService: RecipeService

    @Test
    fun `should validate rest calls and return 401`() {
        // given
        val newRecipeId = "new_recipe_id"
        val userAuthToken = "fake-auth-token"

        every { firebaseUserAuthTokenVerifier.verifyToken(any()) } throws FirebaseAuthException(
            FirebaseException(ErrorCode.PERMISSION_DENIED, "test error", Throwable())
        )

        // when
        mockMvc.post("/api/v1/recipe") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $userAuthToken")
            content = jsonMapper().writeValueAsString(
                CreateRecipeRequest(
                    name = newRecipeId
                )
            )
            accept = MediaType.APPLICATION_JSON
        }
            // expect
            .andExpect {
                status { isUnauthorized() }
            }
    }

    @Disabled("does only work with a fresh workingToken")
    @Test
    fun `should validate rest calls and create recipe`() {
        // given
        val newRecipeName = "new_recipe_id"
        val newRecipeFakeId = "some-fake-id"
        every { recipeService.createRecipe(newRecipeName, any()) } returns ActionResult(
            status = ActionResultStatus.CREATED,
            id = newRecipeFakeId,
        )
        val workingToken =
            "eyJhbGciOiJSUzI1NiIsImtpZCI6IjY3YmFiYWFiYTEwNWFkZDZiM2ZiYjlmZjNmZjVmZTNkY2E0Y2VkYTEiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vcmVjaXB5LTliOGFkIiwiYXVkIjoicmVjaXB5LTliOGFkIiwiYXV0aF90aW1lIjoxNjg2OTIwNjk1LCJ1c2VyX2lkIjoieUY2RFFHRnYyNk9vd2V5dVFXNDdpQjd5UDdGMiIsInN1YiI6InlGNkRRR0Z2MjZPb3dleXVRVzQ3aUI3eVA3RjIiLCJpYXQiOjE2ODY5MjA2OTUsImV4cCI6MTY4NjkyNDI5NSwiZW1haWwiOiJ0ZXN0QHRlc3QuZGUiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsImZpcmViYXNlIjp7ImlkZW50aXRpZXMiOnsiZW1haWwiOlsidGVzdEB0ZXN0LmRlIl19LCJzaWduX2luX3Byb3ZpZGVyIjoicGFzc3dvcmQifX0.CtzTvoQwBn5N7PFpXtFMqvM1xSt1DNgrtKWqjbP8spH4h_DSszURfUYreQwekg2YaFeupQRS9puEoj_S4aHyb5upkOu5dYHUUAHIhTDvlM7FrHmjo9yEqVKz1r_pjAlPPmR4b6byiuvthsecKN4NEMe_urtHoxNite_JSjyUH2suuU7fhHZ8UgKFAYlpVKaiJSsZ19uVfXKY4WrfQbSvnwJmCyQY-uH4a4z4Yay0jDNA-cW9zdLZTrpL6Nh0Jv9MeVXBqxlIqmT7cfTS6IUOIox1-NkU5PnQ_ZLZcY0m38nsN5179JZJMMcM-cEVaXOTuJQY82z5fi7a_KXGHEWsEA"

        // when
        mockMvc.post("/api/v1/recipe") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $workingToken")
            content = jsonMapper().writeValueAsString(
                CreateRecipeRequest(
                    name = newRecipeName
                )
            )
            accept = MediaType.APPLICATION_JSON
        }
            // expect
            .andExpect {
                status { isCreated() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content { jsonPath("id").value(newRecipeFakeId) }
            }
    }

    @Test
    fun `should not validate on open endpoint`() {
        // given
        val recipeId = "fake-recipeId"
        every { recipeService.getAllRecipes() } returns listOf(
            RecipeDto(
                recipeId = recipeId,
                name = "fake-name",
                ingredientUsages = setOf(),
                preparationSteps = listOf(),
                recipeImages = listOf(),
                creator = "fake-creator",
                created = 0L,
            )
        )

        // when
        mockMvc.get("/api/v1/recipes") {
            accept = MediaType.APPLICATION_JSON
        }
            // expect
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content { jsonPath("recipeId").value(recipeId) }
            }
    }

}