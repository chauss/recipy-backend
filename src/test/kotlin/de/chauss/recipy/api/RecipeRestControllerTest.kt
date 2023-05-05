package de.chauss.recipy.api

import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.ninjasquad.springmockk.MockkBean
import de.chauss.recipy.config.FirebaseConfig
import de.chauss.recipy.service.ActionResult
import de.chauss.recipy.service.ActionResultStatus
import de.chauss.recipy.service.RecipeService
import de.chauss.recipy.service.dtos.RecipeDto
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import java.util.*

@WebMvcTest
@ContextConfiguration(classes = [RecipeRestController::class])
class RecipeRestControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    lateinit var recipeService: RecipeService

    @MockkBean
    lateinit var userAuthTokenVerifier: UserAuthTokenVerifier

    @MockkBean
    lateinit var firebaseConfig: FirebaseConfig

    @BeforeEach
    fun setup() {
        every { userAuthTokenVerifier.verifyToken(any()) } returns "fake-user-id"
    }

    @Test
    fun `call to createRecipe endpoint returns created recipeId`() {
        // given
        val newRecipeId = "new_recipe_id"
        val userAuthToken = "fake-auth-token"
        val createRecipeResult =
            ActionResult(status = ActionResultStatus.CREATED, id = newRecipeId)
        every { recipeService.createRecipe(any(), any()) } returns createRecipeResult

        // when
        mockMvc.post("/api/v1/recipe") {
            contentType = MediaType.APPLICATION_JSON
            content = jsonMapper().writeValueAsString(
                CreateRecipeRequest(
                    name = newRecipeId,
                    userToken = userAuthToken
                )
            )
            accept = MediaType.APPLICATION_JSON
        }
            // expect
            .andExpect {
                status { isCreated() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content { jsonPath("id").value(newRecipeId) }
            }
    }

    @Test
    fun `call to createRecipe endpoint returns reason if not created`() {
        // given
        val newRecipeId = "new_recipe_id"
        val userAuthToken = "fake-auth-token"
        val createRecipeResult =
            ActionResult(status = ActionResultStatus.ALREADY_EXISTS, message = "Duplicate")
        every { recipeService.createRecipe(any(), any()) } returns createRecipeResult

        // when
        mockMvc.post("/api/v1/recipe") {
            contentType = MediaType.APPLICATION_JSON
            content = jsonMapper().writeValueAsString(
                CreateRecipeRequest(
                    name = newRecipeId,
                    userToken = userAuthToken
                )
            )
            accept = MediaType.APPLICATION_JSON
        }
            // expect
            .andExpect {
                status { isConflict() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content { jsonPath("message").isNotEmpty }
                content { jsonPath("id").isEmpty }
            }
    }

    @Test
    fun `call to getRecipes endpoint returns recipes`() {
        // given
        val recipeIdOne = "recipe_id_one"
        val recipeNameOne = "name_one"
        val recipeIdTwo = "recipe_id_two"
        val recipeNameTwo = "name_two"
        val recipes: List<RecipeDto> = listOf(
            RecipeDto(
                recipeId = recipeIdOne,
                name = recipeNameOne,
                ingredientUsages = Collections.emptySet(),
                preparationSteps = Collections.emptyList(),
                recipeImages = Collections.emptyList(),
                creator = "fake-userId",
                created = 123
            ),
            RecipeDto(
                recipeId = recipeIdTwo,
                name = recipeNameTwo,
                ingredientUsages = Collections.emptySet(),
                preparationSteps = Collections.emptyList(),
                recipeImages = Collections.emptyList(),
                creator = "fake-userId",
                created = 234
            ),
        )
        every { recipeService.getAllRecipes() } returns recipes

        // when
        mockMvc.get("/api/v1/recipes") {
            accept = MediaType.APPLICATION_JSON
        }
            // expect
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content { jsonPath("$").isArray }
                content { jsonPath("$[0].recipeId").value(recipeIdOne) }
                content { jsonPath("$[0].name").value(recipeNameOne) }
                content { jsonPath("$[1].recipeId").value(recipeIdTwo) }
                content { jsonPath("$[1].recipeId").value(recipeNameTwo) }
            }
    }
}