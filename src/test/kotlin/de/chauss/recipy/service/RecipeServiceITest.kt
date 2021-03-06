package de.chauss.recipy.service

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
            "Kr??ftig umr??hren bis die So??e sich richtig verbindet. Dabei darauf achten, dass die Pilze nicht kaputt gehen da sonst ein Brei entsteht"

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
            "Kr??ftig umr??hren bis die So??e sich richtig verbindet. Dabei darauf achten, dass die Pilze nicht kaputt gehen da sonst ein Brei entsteht"

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
            "Kr??ftig umr??hren bis die So??e sich richtig verbindet. Dabei darauf achten, dass die Pilze nicht kaputt gehen da sonst ein Brei entsteht"
        val newStepNumber = 2
        val newStepDescription =
            "Heftig umr??hren bis die So??e sich richtig verfl??ssigt. Dabei darauf achten, dass die Pilze nicht zerst??rt werden da sonst ein Brei entsteht"

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