package de.chauss.recipy.service

import com.ninjasquad.springmockk.MockkBean
import de.chauss.recipy.TestObjects
import de.chauss.recipy.config.FirebaseConfig
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class RecipeServiceITest(
    @Autowired val recipeService: RecipeService,
    @Autowired val jdbc: JdbcTemplate
) {

    @MockkBean
    lateinit var firebaseConfig: FirebaseConfig

    @BeforeEach
    @AfterEach
    fun cleanup() {
        jdbc.execute("DELETE FROM preparation_steps WHERE TRUE")
        jdbc.execute("DELETE FROM ingredient_usages WHERE TRUE")
        jdbc.execute("DELETE FROM recipes WHERE TRUE")
    }

    @Test
    fun `save a recipe and find it again`() {
        // given
        val recipeNameToSave = "Kartoffelauflauf"

        // when
        val result = recipeService.createRecipe(recipeNameToSave, TestObjects.TEST_USER_ID)

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
        val creationResult = recipeService.createRecipe(recipeNameToSave, TestObjects.TEST_USER_ID)
        assertEquals(creationResult.status, ActionResultStatus.CREATED)
        assertNotNull(creationResult.id)
        val recipeId = creationResult.id!!
        val recipeFound = recipeService.getRecipeById(recipeId)!!
        assertEquals(recipeFound.name, recipeNameToSave)

        // when
        val deletionResult = recipeService.deleteRecipeById(recipeId, TestObjects.TEST_USER_ID)

        // expect
        assertEquals(ActionResultStatus.DELETED, deletionResult.status)
        val recipeFoundAgain = recipeService.getRecipeById(recipeId)
        assertNull(recipeFoundAgain)
    }

    @Test
    fun `deleting an unknown recipe returns ELEMENT_NOT_FOUND`() {
        // given
        val unknownRecipeId = "unknown_recipe_id"

        // when
        val deletionResult =
            recipeService.deleteRecipeById(unknownRecipeId, TestObjects.TEST_USER_ID)

        // expect
        assertEquals(ActionResultStatus.ELEMENT_NOT_FOUND, deletionResult.status)
    }

    @Test
    fun `deleting another users recipe returns UNAUTHORIZED and recipe is not deleted`() {
        // given
        val result = recipeService.createRecipe("Recipe of User One", TestObjects.TEST_USER_ID)
        val recipeId = result.id!!

        // when
        val deletionResult = recipeService.deleteRecipeById(recipeId, "different_user_id")

        // expect
        assertEquals(ActionResultStatus.UNAUTHORIZED, deletionResult.status)
        val recipeWrongUserTriedToDelete = recipeService.getRecipeById(recipeId)
        assertNotNull(recipeWrongUserTriedToDelete)
    }

    @Test
    fun `created preparationStep is found again`() {
        // given
        val recipeNameToSave = "Recipe with step"
        val stepNumber = 1
        val stepDescription =
            "Kräftig umrühren bis die Soße sich richtig verbindet. Dabei darauf achten, dass die Pilze nicht kaputt gehen da sonst ein Brei entsteht"

        val recipeCreateResult =
            recipeService.createRecipe(recipeNameToSave, TestObjects.TEST_USER_ID)
        assertEquals(ActionResultStatus.CREATED, recipeCreateResult.status)
        assertNotNull(recipeCreateResult.id)
        val recipeId = recipeCreateResult.id!!

        // when
        val result = recipeService.createPreparationStep(
            recipeId = recipeId,
            stepNumber = stepNumber,
            description = stepDescription,
            userId = TestObjects.TEST_USER_ID
        )

        // expect
        assertEquals(result.status, ActionResultStatus.CREATED)
        assertNotNull(result.id)
        val preparationStepId = result.id!!

        val recipeFound = recipeService.getRecipeById(recipeId)!!
        assertEquals(1, recipeFound.preparationSteps.size)
        assertEquals(preparationStepId, recipeFound.preparationSteps.first().preparationStepId)
        assertEquals(recipeId, recipeFound.preparationSteps.first().recipeId)
        assertEquals(stepNumber, recipeFound.preparationSteps.first().stepNumber)
        assertEquals(stepDescription, recipeFound.preparationSteps.first().description)
        assertEquals(recipeNameToSave, recipeFound.name)
    }

    @Test
    fun `deleting recipe also deletes connected preparationStep`() {
        // given
        val recipeNameToSave = "Recipe with step to be deleted"
        val stepNumber = 1
        val stepDescription =
            "Kräftig umrühren bis die Soße sich richtig verbindet. Dabei darauf achten, dass die Pilze nicht kaputt gehen da sonst ein Brei entsteht"

        val recipeCreateResult =
            recipeService.createRecipe(recipeNameToSave, TestObjects.TEST_USER_ID)
        assertEquals(ActionResultStatus.CREATED, recipeCreateResult.status)
        assertNotNull(recipeCreateResult.id)
        val recipeId = recipeCreateResult.id!!
        val createPreparationStepResult = recipeService.createPreparationStep(
            recipeId = recipeId,
            stepNumber = stepNumber,
            description = stepDescription,
            userId = TestObjects.TEST_USER_ID
        )
        assertEquals(ActionResultStatus.CREATED, createPreparationStepResult.status)
        assertNotNull(createPreparationStepResult.id)
        val preparationStepId = createPreparationStepResult.id!!

        // when
        val deleteResult = recipeService.deleteRecipeById(recipeId, TestObjects.TEST_USER_ID)

        // expect
        assertEquals(ActionResultStatus.DELETED, deleteResult.status)
        assertEquals(recipeId, deleteResult.id)

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

        val recipeCreateResult =
            recipeService.createRecipe(recipeNameToSave, TestObjects.TEST_USER_ID)
        assertEquals(ActionResultStatus.CREATED, recipeCreateResult.status)
        assertNotNull(recipeCreateResult.id)
        val recipeId = recipeCreateResult.id!!
        val createPreparationStepResult = recipeService.createPreparationStep(
            recipeId = recipeId,
            stepNumber = stepNumber,
            description = stepDescription,
            userId = TestObjects.TEST_USER_ID,
        )
        assertEquals(ActionResultStatus.CREATED, createPreparationStepResult.status)
        assertNotNull(createPreparationStepResult.id)
        val preparationStepId = createPreparationStepResult.id!!

        // when
        val updateResult = recipeService.updatePreparationStep(
            preparationStepId = preparationStepId,
            stepNumber = newStepNumber,
            description = newStepDescription,
            userId = TestObjects.TEST_USER_ID,
        )

        // expect
        assertEquals(ActionResultStatus.UPDATED, updateResult.status)
        assertEquals(preparationStepId, updateResult.id)

        val foundPreparationStep = recipeService.getPreparationStepById(preparationStepId)
        assertNotNull(foundPreparationStep)
        assertEquals(preparationStepId, foundPreparationStep?.preparationStepId)
        assertEquals(newStepNumber, foundPreparationStep?.stepNumber)
        assertEquals(newStepDescription, foundPreparationStep?.description)
    }
}