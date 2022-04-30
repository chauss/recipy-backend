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
class RecipeServiceITest(
    @Autowired val recipeService: RecipeService,
    @Autowired val jdbc: JdbcTemplate
) {

    @AfterEach
    fun cleanup() {
        jdbc.execute("DELETE FROM preparation_steps WHERE TRUE")
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

        // expect
        assertEquals(result.status, ActionResultStatus.CREATED)
        assertNotNull(result.id)
        val recipeId = result.id!!

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
        // given
        val unknownRecipeId = "unknown_recipe_id"

        // when
        val deletionResult = recipeService.deleteRecipeById(unknownRecipeId)

        // expect
        assertEquals(deletionResult.status, ActionResultStatus.ELEMENT_NOT_FOUND)
    }

    @Test
    fun `created preparationStep is found again`() {
        // given
        val recipeNameToSave = "Recipe with step"
        val stepNumber = 1
        val stepDescription =
            "Kräftig umrühren bis die Soße sich richtig verbindet. Dabei darauf achten, dass die Pilze nicht kaputt gehen da sonst ein Brei entsteht"

        val recipeCreateResult = recipeService.createRecipe(recipeNameToSave)
        assertEquals(recipeCreateResult.status, ActionResultStatus.CREATED)
        assertNotNull(recipeCreateResult.id)
        val recipeId = recipeCreateResult.id!!

        // when
        val result = recipeService.createPreparationStep(
            recipeId = recipeId,
            stepNumber = stepNumber,
            description = stepDescription,
        )

        // expect
        assertEquals(result.status, ActionResultStatus.CREATED)
        assertNotNull(result.id)
        val preparationStepId = result.id!!

        val recipeFound = recipeService.getRecipeById(recipeId)!!
        assertEquals(recipeFound.preparationSteps.size, 1)
        assertEquals(recipeFound.preparationSteps.first().preparationStepId, preparationStepId)
        assertEquals(recipeFound.preparationSteps.first().recipeId, recipeId)
        assertEquals(recipeFound.preparationSteps.first().stepNumber, stepNumber)
        assertEquals(recipeFound.preparationSteps.first().description, stepDescription)
        assertEquals(recipeFound.name, recipeNameToSave)
    }

    @Test
    fun `deleting recipe also deletes connected preparationStep`() {
        // given
        val recipeNameToSave = "Recipe with step to be deleted"
        val stepNumber = 1
        val stepDescription =
            "Kräftig umrühren bis die Soße sich richtig verbindet. Dabei darauf achten, dass die Pilze nicht kaputt gehen da sonst ein Brei entsteht"

        val recipeCreateResult = recipeService.createRecipe(recipeNameToSave)
        assertEquals(recipeCreateResult.status, ActionResultStatus.CREATED)
        assertNotNull(recipeCreateResult.id)
        val recipeId = recipeCreateResult.id!!
        val createPreparationStepResult = recipeService.createPreparationStep(
            recipeId = recipeId,
            stepNumber = stepNumber,
            description = stepDescription,
        )
        assertEquals(createPreparationStepResult.status, ActionResultStatus.CREATED)
        assertNotNull(createPreparationStepResult.id)
        val preparationStepId = createPreparationStepResult.id!!

        // when
        val deleteResult = recipeService.deleteRecipeById(recipeId)

        // expect
        assertEquals(deleteResult.status, ActionResultStatus.DELETED)
        assertEquals(deleteResult.id, recipeId)

        val foundPreparationStep = recipeService.getPreparationStepById(preparationStepId)
        assertNull(foundPreparationStep)
    }

    @Test
    fun `update preparationStep works`() {
        // given
        val recipeNameToSave = "Recipe with step to be updated"
        val stepNumber = 1
        val stepDescription =
            "Kräftig umrühren bis die Soße sich richtig verbindet. Dabei darauf achten, dass die Pilze nicht kaputt gehen da sonst ein Brei entsteht"
        val newStepNumber = 2
        val newStepDescription =
            "Heftig umrühren bis die Soße sich richtig verflüssigt. Dabei darauf achten, dass die Pilze nicht zerstört werden da sonst ein Brei entsteht"

        val recipeCreateResult = recipeService.createRecipe(recipeNameToSave)
        assertEquals(recipeCreateResult.status, ActionResultStatus.CREATED)
        assertNotNull(recipeCreateResult.id)
        val recipeId = recipeCreateResult.id!!
        val createPreparationStepResult = recipeService.createPreparationStep(
            recipeId = recipeId,
            stepNumber = stepNumber,
            description = stepDescription,
        )
        assertEquals(createPreparationStepResult.status, ActionResultStatus.CREATED)
        assertNotNull(createPreparationStepResult.id)
        val preparationStepId = createPreparationStepResult.id!!

        // when
        val updateResult = recipeService.updatePreparationStep(
            preparationStepId = preparationStepId,
            stepNumber = newStepNumber,
            description = newStepDescription
        )

        // expect
        assertEquals(updateResult.status, ActionResultStatus.UPDATED)
        assertEquals(updateResult.id, preparationStepId)

        val foundPreparationStep = recipeService.getPreparationStepById(preparationStepId)
        assertNotNull(foundPreparationStep)
        assertEquals(foundPreparationStep?.preparationStepId, preparationStepId)
        assertEquals(foundPreparationStep?.preparationStepId, preparationStepId)
        assertEquals(foundPreparationStep?.preparationStepId, preparationStepId)
    }
}