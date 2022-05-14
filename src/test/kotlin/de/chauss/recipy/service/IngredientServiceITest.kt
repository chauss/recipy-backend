package de.chauss.recipy.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestMethodOrder(OrderAnnotation::class)
class IngredientTest(
    @Autowired val ingredientService: IngredientService,
    @Autowired val recipeService: RecipeService,
) {
    val ingredientUnitName = "Prise"
    val ingredientName = "Olivenöl"

    @Test
    @Order(2)
    fun `created ingredientUnit is found again`() {
        // when
        val result = ingredientService.createIngredientUnit(ingredientUnitName)
        assertEquals(result.status, ActionResultStatus.CREATED)
        val ingredientUnitId = result.id!!

        val ingredientUnitFound = ingredientService.getIngredientUnitById(ingredientUnitId)

        // expect
        assertEquals(ingredientUnitFound?.name, ingredientUnitName)
    }

    @Test
    @Order(3)
    fun `created ingredient is found again`() {
        // when
        val result = ingredientService.createIngredient(ingredientName)

        // expect
        assertEquals(result.status, ActionResultStatus.CREATED)
        assertNotNull(result.id)
        val ingredientFound = ingredientService.getIngredientById(result.id!!)
        assertEquals(ingredientFound?.name, ingredientName)
    }

    @Test
    @Order(4)
    fun `created ingredientUsage is found again`() {
        // given
        val ingredientUsageAmount = 2.0
        val recipeCreationResult = recipeService.createRecipe("Kartoffelauflauf")
        val recipeId = recipeCreationResult.id!!

        val existingIngredient = ingredientService.findIngredientByName(ingredientName)!!
        val existingIngredientUnit =
            ingredientService.findIngredientUnitByName(ingredientUnitName)!!

        // when
        val result = ingredientService.createIngredientUsage(
            recipeId,
            ingredientId = existingIngredient.ingredientId,
            ingredientUnitId = existingIngredientUnit.ingredientUnitId,
            amount = ingredientUsageAmount
        )

        // expect
        assertEquals(result.status, ActionResultStatus.CREATED)
        assertNotNull(result.id)
        val ingredientUsageFound = ingredientService.getIngredientUsageById(result.id!!)
        assertEquals(ingredientUsageFound!!.ingredientUsageId, result.id)
        assertEquals(ingredientUsageFound.ingredientId, existingIngredient.ingredientId)
        assertEquals(ingredientUsageFound.ingredientUnitId, existingIngredientUnit.ingredientUnitId)
        assertEquals(ingredientUsageFound.amount, ingredientUsageAmount)
    }

    @Test
    @Order(5)
    fun `recipe can't have two ingredientUsages with the same ingredientId`() {
        // given
        val ingredientUsageAmount = 2.0
        val recipeCreationResult = recipeService.createRecipe("Kartoffelsalat")
        assertNotNull(recipeCreationResult.id)
        val recipeId = recipeCreationResult.id!!

        val existingIngredient = ingredientService.findIngredientByName(ingredientName)!!

        val existingIngredientUnit =
            ingredientService.findIngredientUnitByName(ingredientUnitName)!!

        // when
        val successResult = ingredientService.createIngredientUsage(
            recipeId,
            ingredientId = existingIngredient.ingredientId,
            ingredientUnitId = existingIngredientUnit.ingredientUnitId,
            amount = ingredientUsageAmount
        )
        val failureResult = ingredientService.createIngredientUsage(
            recipeId,
            ingredientId = existingIngredient.ingredientId,
            ingredientUnitId = existingIngredientUnit.ingredientUnitId,
            amount = 3.0
        )

        // expect
        assertEquals(successResult.status, ActionResultStatus.CREATED)
        assertNotNull(successResult.id)
        assertEquals(failureResult.status, ActionResultStatus.INVALID_ARGUMENTS)
        assertNull(failureResult.id)
        val ingredientUsageFound = ingredientService.getIngredientUsageById(successResult.id!!)!!
        assertEquals(ingredientUsageFound.ingredientUsageId, successResult.id)
        assertEquals(ingredientUsageFound.ingredientId, existingIngredient.ingredientId)
        assertEquals(ingredientUsageFound.ingredientUnitId, existingIngredientUnit.ingredientUnitId)
        assertEquals(ingredientUsageFound.amount, ingredientUsageAmount)
    }

    @Test
    @Order(6)
    fun `can not create two ingredients with the same name (case insensitive)`() {
        // when
        val creationResult = ingredientService.createIngredient(ingredientName.uppercase())

        // expect
        assertEquals(creationResult.status, ActionResultStatus.ALREADY_EXISTS)
        assertNotEquals(creationResult.message, "")
        assertNull(creationResult.id)
    }

    @Test
    @Order(7)
    fun `can not create two ingredientsUnits with the same name (case insensitive)`() {
        // when
        val creationResult = ingredientService.createIngredientUnit(ingredientUnitName.uppercase())

        // expect
        assertEquals(creationResult.status, ActionResultStatus.ALREADY_EXISTS)
        assertNotEquals(creationResult.message, "")
        assertNull(creationResult.id)
    }

    @Test
    @Order(8)
    fun `deleting used ingredientUnit is not possible`() {
        // given
        val ingredientUnitId =
            ingredientService.findIngredientUnitByName(ingredientUnitName)!!.ingredientUnitId

        // when
        val result = ingredientService.deleteIngredientUnitById(ingredientUnitId)

        // expect
        assertEquals(result.status, ActionResultStatus.FAILED_TO_DELETE)
        val triedToDeleteIngredientUnit =
            ingredientService.findIngredientUnitByName(ingredientUnitName)
        assertNotNull(triedToDeleteIngredientUnit)
    }

    @Test
    @Order(9)
    fun `deleting used ingredient is not possible`() {
        // given
        val ingredientId =
            ingredientService.findIngredientByName(ingredientName)!!.ingredientId

        // when
        val result = ingredientService.deleteIngredientById(ingredientId)

        // expect
        assertEquals(result.status, ActionResultStatus.FAILED_TO_DELETE)
        val triedToDeleteIngredient =
            ingredientService.findIngredientByName(ingredientName)
        assertNotNull(triedToDeleteIngredient)
    }

    @Test
    @Order(10)
    fun `deleted ingredientUsage is not found again`() {
        // given
        val ingredientUsageAmount = 3.5
        val recipeCreationResult = recipeService.createRecipe("Hühnersuppe")
        assertEquals(recipeCreationResult.status, ActionResultStatus.CREATED)
        assertNotNull(recipeCreationResult.id)
        val recipeId = recipeCreationResult.id!!

        val existingIngredient = ingredientService.findIngredientByName(ingredientName)!!

        val existingIngredientUnit =
            ingredientService.findIngredientUnitByName(ingredientUnitName)!!

        val creationResult = ingredientService.createIngredientUsage(
            recipeId,
            ingredientId = existingIngredient.ingredientId,
            ingredientUnitId = existingIngredientUnit.ingredientUnitId,
            amount = ingredientUsageAmount
        )
        val ingredientUsageFound = ingredientService.getIngredientUsageById(creationResult.id!!)
        assertNotNull(ingredientUsageFound)
        val ingredientUsageId = ingredientUsageFound!!.ingredientUsageId

        // when
        ingredientService.deleteIngredientUsageById(ingredientUsageId)

        // expect
        val deletedIngredientUsageFinding =
            ingredientService.getIngredientUsageById(ingredientUsageId)
        assertNull(deletedIngredientUsageFinding)
    }

    @Test
    @Order(11)
    fun `can update the amount, unit and ingredient of an ingredientUsage`() {
        // given
        val ingredientUsageAmount = 2.0
        val recipeCreationResult = recipeService.createRecipe("Toller Kartoffelsalat")
        assertEquals(recipeCreationResult.status, ActionResultStatus.CREATED)
        assertNotNull(recipeCreationResult.id)
        val recipeId = recipeCreationResult.id!!

        val existingIngredient = ingredientService.findIngredientByName(ingredientName)!!
        val existingIngredientUnit =
            ingredientService.findIngredientUnitByName(ingredientUnitName)!!

        val ingredientUnitCreationResult = ingredientService.createIngredientUnit("Neue Einheit!")
        assertEquals(ingredientUnitCreationResult.status, ActionResultStatus.CREATED)
        val newIngredientUnitId = ingredientUnitCreationResult.id!!

        val ingredientCreationResult = ingredientService.createIngredient("Neue Zutat!")
        assertEquals(ingredientCreationResult.status, ActionResultStatus.CREATED)
        val newIngredientId = ingredientCreationResult.id!!

        val newAmount = ingredientUsageAmount + 2

        val creationResult = ingredientService.createIngredientUsage(
            recipeId,
            ingredientId = existingIngredient.ingredientId,
            ingredientUnitId = existingIngredientUnit.ingredientUnitId,
            amount = ingredientUsageAmount
        )
        val ingredientUsageFound = ingredientService.getIngredientUsageById(creationResult.id!!)
        assertNotNull(ingredientUsageFound)

        // when
        val updateResult = ingredientService.updateIngredientUsage(
            ingredientUsageId = ingredientUsageFound!!.ingredientUsageId,
            ingredientId = newIngredientId,
            ingredientUnitId = newIngredientUnitId,
            amount = newAmount
        )

        // expect
        assertEquals(updateResult.status, ActionResultStatus.UPDATED)
        assertEquals(updateResult.id, ingredientUsageFound.ingredientUsageId)
        val updatedIngredientUsage =
            ingredientService.getIngredientUsageById(ingredientUsageFound.ingredientUsageId)
        assertNotNull(updatedIngredientUsage!!)
        assertEquals(updatedIngredientUsage.ingredientId, newIngredientId)
        assertEquals(updatedIngredientUsage.ingredientUnitId, newIngredientUnitId)
        assertEquals(updatedIngredientUsage.amount, newAmount)
    }

    @Test
    @Order(12)
    fun `can not update ingredient of an ingredientUsage to an ingredient that already exists on recipe`() {
        // given
        val ingredientUsageAmount = 2.0
        val recipeCreationResult = recipeService.createRecipe("Tollster Kartoffelsalat")
        assertEquals(recipeCreationResult.status, ActionResultStatus.CREATED)
        assertNotNull(recipeCreationResult.id)
        val recipeId = recipeCreationResult.id!!

        val existingIngredient = ingredientService.findIngredientByName(ingredientName)!!
        val existingIngredientUnit =
            ingredientService.findIngredientUnitByName(ingredientUnitName)!!

        val ingredientUnitCreationResult =
            ingredientService.createIngredientUnit("Neueste Einheit!")
        assertEquals(ingredientUnitCreationResult.status, ActionResultStatus.CREATED)
        val newIngredientUnitId = ingredientUnitCreationResult.id!!

        val ingredientCreationResult = ingredientService.createIngredient("Neueste Zutat!")
        assertEquals(ingredientCreationResult.status, ActionResultStatus.CREATED)
        val newIngredientId = ingredientCreationResult.id!!

        val newAmount = ingredientUsageAmount + 2

        val creationResultOne = ingredientService.createIngredientUsage(
            recipeId,
            ingredientId = existingIngredient.ingredientId,
            ingredientUnitId = existingIngredientUnit.ingredientUnitId,
            amount = ingredientUsageAmount
        )
        val ingredientUsageOne = ingredientService.getIngredientUsageById(creationResultOne.id!!)
        assertNotNull(ingredientUsageOne!!)

        val creationResultTwo = ingredientService.createIngredientUsage(
            recipeId,
            ingredientId = newIngredientId,
            ingredientUnitId = newIngredientUnitId,
            amount = newAmount
        )
        assertNotNull(creationResultTwo)

        val ingredientUsageTwo = ingredientService.getIngredientUsageById(creationResultTwo.id!!)!!

        // when
        val updateResult = ingredientService.updateIngredientUsage(
            ingredientUsageId = ingredientUsageOne.ingredientUsageId,
            ingredientId = ingredientUsageTwo.ingredientId,
            ingredientUnitId = ingredientUsageOne.ingredientUnitId,
            amount = ingredientUsageOne.amount
        )

        // expect
        assertEquals(updateResult.status, ActionResultStatus.INVALID_ARGUMENTS)
        assertNull(updateResult.id)
        val updatedIngredientUsage =
            ingredientService.getIngredientUsageById(ingredientUsageOne.ingredientUsageId)
        assertNotNull(updatedIngredientUsage!!)
        assertEquals(updatedIngredientUsage.ingredientId, ingredientUsageOne.ingredientId)
        assertEquals(updatedIngredientUsage.ingredientUnitId, ingredientUsageOne.ingredientUnitId)
        assertEquals(updatedIngredientUsage.amount, ingredientUsageOne.amount)
    }

    @Test
    @Order(13)
    fun `deleting a recipe also deletes the connected ingredientUsages`() {
        // given
        val ingredientUsageAmount = 2.0
        val recipeCreationResult = recipeService.createRecipe("Eindeutiger Kartoffelsalat")
        assertEquals(recipeCreationResult.status, ActionResultStatus.CREATED)
        assertNotNull(recipeCreationResult.id)
        val recipeId = recipeCreationResult.id!!

        val existingIngredient = ingredientService.findIngredientByName(ingredientName)!!

        val existingIngredientUnit =
            ingredientService.findIngredientUnitByName(ingredientUnitName)!!

        val creationResult = ingredientService.createIngredientUsage(
            recipeId,
            ingredientId = existingIngredient.ingredientId,
            ingredientUnitId = existingIngredientUnit.ingredientUnitId,
            amount = ingredientUsageAmount
        )
        val ingredientUsageFound = ingredientService.getIngredientUsageById(creationResult.id!!)
        assertNotNull(ingredientUsageFound)

        // when
        val deletionResult = recipeService.deleteRecipeById(recipeId)

        // expect
        assertEquals(deletionResult.status, ActionResultStatus.DELETED)
        assertEquals(deletionResult.id, recipeId)
        val deletedIngredientUsageFinding =
            ingredientService.getIngredientUsageById(creationResult.id!!)
        assertNull(deletedIngredientUsageFinding)
    }

    @Test
    fun `deleted ingredientUnit is not found again`() {
        // given
        val ingredientUnitToSave = "Teelöffel"
        val creationResult = ingredientService.createIngredientUnit(ingredientUnitToSave)
        assertEquals(creationResult.status, ActionResultStatus.CREATED)
        assertNotNull(creationResult.id)
        val ingredientUnitId = creationResult.id!!
        val ingredientUnitFound = ingredientService.getIngredientUnitById(ingredientUnitId)!!
        assertEquals(ingredientUnitFound.name, ingredientUnitToSave)

        // when
        val deletionResult = ingredientService.deleteIngredientUnitById(ingredientUnitId)

        // expect
        assertEquals(deletionResult.status, ActionResultStatus.DELETED);
        val ingredientUnitFoundAgain = ingredientService.getIngredientUnitById(ingredientUnitId)
        assertNull(ingredientUnitFoundAgain)
    }

    @Test
    fun `deleting an unknown ingredientUnit returns ELEMENT_NOT_FOUND`() {
        val unknownIngredientUnitId = "unknown_ingredient_unit_id"
        // when
        val deletionResult = ingredientService.deleteIngredientUnitById(unknownIngredientUnitId)

        // expect
        assertEquals(deletionResult.status, ActionResultStatus.ELEMENT_NOT_FOUND)
    }

    @Test
    fun `deleted ingredient is not found again`() {
        // given
        val ingredientToSave = "Pflaumenmarmelade"
        val creationResult = ingredientService.createIngredient(ingredientToSave)
        assertEquals(creationResult.status, ActionResultStatus.CREATED)
        assertNotNull(creationResult.id)
        val ingredientId = creationResult.id!!
        val ingredientFound = ingredientService.getIngredientById(ingredientId)!!
        assertEquals(ingredientFound.name, ingredientToSave)

        // when
        val deletionResult = ingredientService.deleteIngredientById(ingredientId)

        // expect
        assertEquals(deletionResult.status, ActionResultStatus.DELETED);
        val ingredientFoundAgain = ingredientService.getIngredientById(ingredientId)
        assertNull(ingredientFoundAgain)
    }

    @Test
    fun `deleting an unknown ingredient returns ELEMENT_NOT_FOUND`() {
        val unknownIngredientId = "unknown_ingredient_id"
        // when
        val deletionResult = ingredientService.deleteIngredientById(unknownIngredientId)

        // expect
        assertEquals(deletionResult.status, ActionResultStatus.ELEMENT_NOT_FOUND)
    }

    @Test
    fun `deleting an unknown ingredientUsage returns ELEMENT_NOT_FOUND`() {
        val unknownIngredientUsageId = "unknown_ingredient_usage_id"
        // when
        val deletionResult = ingredientService.deleteIngredientUsageById(unknownIngredientUsageId)

        // expect
        assertEquals(deletionResult.status, ActionResultStatus.ELEMENT_NOT_FOUND)
    }
}
