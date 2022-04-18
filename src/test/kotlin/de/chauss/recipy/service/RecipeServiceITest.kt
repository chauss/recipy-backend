package de.chauss.recipy.service

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

fun postgresForRecipes(imageName: String, opts: JdbcDatabaseContainer<Nothing>.() -> Unit) =
    PostgreSQLContainer<Nothing>(DockerImageName.parse(imageName)).apply(opts)

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Testcontainers
@Disabled("Can only run local (not in pipeline)")
class RecipeTest(
    @Autowired val recipeService: RecipeService,
    @Autowired val jdbc: JdbcTemplate
) {

    @AfterEach
    fun cleanup() {
        jdbc.execute("DELETE FROM recipes WHERE TRUE")
    }

    companion object {
        @Container
        val postgresqlContainer = postgresForRecipes("postgres:14.2-alpine") {
            withDatabaseName("recipy-backend-it")
            withUsername("root")
            withPassword("root")
            //withInitScript("sql/schema.sql")
        }

        @JvmStatic
        @DynamicPropertySource
        fun datasourceconfig(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgresqlContainer::getUsername)
            registry.add("spring.datasource.password", postgresqlContainer::getPassword)
        }
    }

    @Test
    fun `container is up and running`() {
        assertTrue(postgresqlContainer.isRunning)
    }

    @Test
    fun `save a recipe and find it again`() {
        // given
        val recipeNameToSave = "Kartoffelauflauf"

        // when
        val result = recipeService.createRecipe(recipeNameToSave)
        assertEquals(result.status, ActionResultStatus.CREATED)
        assertNotNull(result.id)
        val recipeId = result.id!!

        // expect
        val recipeFound = recipeService.getRecipeById(recipeId)!!
        assertEquals(recipeFound.name, recipeNameToSave)
    }

    @Test
    fun `deleted recipe is not found again`() {
        // given
        val recipeNameToSave = "Kartoffelauflauf"
        val creationResult = recipeService.createRecipe(recipeNameToSave)
        assertEquals(creationResult.status, ActionResultStatus.CREATED)
        assertNotNull(creationResult.id)
        val recipeId = creationResult.id!!
        val recipeFound = recipeService.getRecipeById(recipeId)!!
        assertEquals(recipeFound.name, recipeNameToSave)

        // when
        val deletionResult = recipeService.deleteRecipeById(recipeId)

        // expect
        assertEquals(deletionResult.status, ActionResultStatus.DELETED);
        val recipeFoundAgain = recipeService.getRecipeById(recipeId)
        assertNull(recipeFoundAgain)
    }

    @Test
    fun `deleting an unknown recipe returns ELEMENT_NOT_FOUND`() {
        val unknownRecipeId = "unknown_recipe_id"
        // when
        val deletionResult = recipeService.deleteRecipeById(unknownRecipeId)

        // expect
        assertEquals(deletionResult.status, ActionResultStatus.ELEMENT_NOT_FOUND)
    }
}