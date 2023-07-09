package de.chauss.recipy.service

import com.ninjasquad.springmockk.MockkBean
import de.chauss.recipy.TestObjects
import de.chauss.recipy.config.FirebaseConfig
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestMethodOrder(OrderAnnotation::class)
@ActiveProfiles("test")
class IngredientServiceITest(
    @Autowired val ingredientService: IngredientService,
    @Autowired val recipeService: RecipeService,
) {
    private val ingredientUnitName = "Prise"
    private val ingredientName = "Olivenöl"

    @MockkBean
    lateinit var firebaseConfig: FirebaseConfig

    @Test
    @Order(2)
    fun `created ingredientUnit is found again`() {
        // when
        val result =
            ingredientService.createIngredientUnit(ingredientUnitName, TestObjects.TEST_USER_ID)
        assertEquals(ActionResultStatus.CREATED, result.status)
        val ingredientUnitId = result.id!!

        val ingredientUnitFound = ingredientService.getIngredientUnitById(ingredientUnitId)

        // expect
        assertEquals(ingredientUnitName, ingredientUnitFound?.name)
    }

    @Test
    @Order(3)
    fun `created ingredient is found again`() {
        // when
        val result = ingredientService.createIngredient(ingredientName, TestObjects.TEST_USER_ID)

        // expect
        assertEquals(ActionResultStatus.CREATED, result.status)
        assertNotNull(result.id)
        val ingredientFound = ingredientService.getIngredientById(result.id!!)
        assertEquals(ingredientName, ingredientFound?.name)
    }

    @Test
    @Order(4)
    fun `created ingredientUsage is found again`() {
        // given
        val ingredientUsageAmount = 2.0
        val recipeCreationResult =
            recipeService.createRecipe("Kartoffelauflauf", TestObjects.TEST_USER_ID)
        val recipeId = recipeCreationResult.id!!

        val existingIngredient = ingredientService.findIngredientByName(ingredientName)!!
        val existingIngredientUnit =
            ingredientService.findIngredientUnitByName(ingredientUnitName)!!

        // when
        val result = ingredientService.createIngredientUsage(
            recipeId,
            ingredientId = existingIngredient.ingredientId,
            ingredientUnitId = existingIngredientUnit.ingredientUnitId,
            amount = ingredientUsageAmount,
            userId = TestObjects.TEST_USER_ID,
        )

        // expect
        assertEquals(ActionResultStatus.CREATED, result.status)
        assertNotNull(result.id)
        val ingredientUsageFound = ingredientService.getIngredientUsageById(result.id!!)
        assertEquals(result.id, ingredientUsageFound!!.ingredientUsageId)
        assertEquals(existingIngredient.ingredientId, ingredientUsageFound.ingredientId)
        assertEquals(existingIngredientUnit.ingredientUnitId, ingredientUsageFound.ingredientUnitId)
        assertEquals(ingredientUsageAmount, ingredientUsageFound.amount)
    }

    @Test
    @Order(5)
    fun `recipe can't have two ingredientUsages with the same ingredientId`() {
        // given
        val ingredientUsageAmount = 2.0
        val recipeCreationResult =
            recipeService.createRecipe("Kartoffelsalat", TestObjects.TEST_USER_ID)
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
            amount = ingredientUsageAmount,
            userId = TestObjects.TEST_USER_ID,
        )
        val failureResult = ingredientService.createIngredientUsage(
            recipeId,
            ingredientId = existingIngredient.ingredientId,
            ingredientUnitId = existingIngredientUnit.ingredientUnitId,
            amount = 3.0,
            userId = TestObjects.TEST_USER_ID,
        )

        // expect
        assertEquals(ActionResultStatus.CREATED, successResult.status)
        assertNotNull(successResult.id)
        assertEquals(ActionResultStatus.INVALID_ARGUMENTS, failureResult.status)
        assertNull(failureResult.id)
        val ingredientUsageFound = ingredientService.getIngredientUsageById(successResult.id!!)!!
        assertEquals(successResult.id, ingredientUsageFound.ingredientUsageId)
        assertEquals(existingIngredient.ingredientId, ingredientUsageFound.ingredientId)
        assertEquals(existingIngredientUnit.ingredientUnitId, ingredientUsageFound.ingredientUnitId)
        assertEquals(ingredientUsageAmount, ingredientUsageFound.amount)
    }

    @Test
    @Order(6)
    fun `can not create two ingredients with the same name (case insensitive)`() {
        // when
        val creationResult =
            ingredientService.createIngredient(ingredientName.uppercase(), TestObjects.TEST_USER_ID)

        // expect
        assertEquals(ActionResultStatus.ALREADY_EXISTS, creationResult.status)
        assertNotEquals(creationResult.message, "")
        assertNull(creationResult.id)
    }

    @Test
    @Order(7)
    fun `can not create two ingredientsUnits with the same name (case insensitive)`() {
        // when
        val creationResult = ingredientService.createIngredientUnit(
            ingredientUnitName.uppercase(),
            TestObjects.TEST_USER_ID
        )

        // expect
        assertEquals(ActionResultStatus.ALREADY_EXISTS, creationResult.status)
        assertNotEquals("", creationResult.message)
        assertNull(creationResult.id)
    }

    @Test
    @Order(8)
    fun `deleting used ingredientUnit is not possible`() {
        // given
        val ingredientUnitId =
            ingredientService.findIngredientUnitByName(ingredientUnitName)!!.ingredientUnitId

        // when
        val result =
            ingredientService.deleteIngredientUnitById(ingredientUnitId, TestObjects.TEST_USER_ID)

        // expect
        assertEquals(ActionResultStatus.FAILED_TO_DELETE, result.status)
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
        val result = ingredientService.deleteIngredientById(ingredientId, TestObjects.TEST_USER_ID)

        // expect
        assertEquals(ActionResultStatus.FAILED_TO_DELETE, result.status)
        val triedToDeleteIngredient =
            ingredientService.findIngredientByName(ingredientName)
        assertNotNull(triedToDeleteIngredient)
    }

    @Test
    @Order(10)
    fun `deleted ingredientUsage is not found again`() {
        // given
        val ingredientUsageAmount = 3.5
        val recipeCreationResult =
            recipeService.createRecipe("Hühnersuppe", TestObjects.TEST_USER_ID)
        assertEquals(ActionResultStatus.CREATED, recipeCreationResult.status)
        assertNotNull(recipeCreationResult.id)
        val recipeId = recipeCreationResult.id!!

        val existingIngredient = ingredientService.findIngredientByName(ingredientName)!!

        val existingIngredientUnit =
            ingredientService.findIngredientUnitByName(ingredientUnitName)!!

        val creationResult = ingredientService.createIngredientUsage(
            recipeId,
            ingredientId = existingIngredient.ingredientId,
            ingredientUnitId = existingIngredientUnit.ingredientUnitId,
            amount = ingredientUsageAmount,
            userId = TestObjects.TEST_USER_ID,
        )
        val ingredientUsageFound = ingredientService.getIngredientUsageById(creationResult.id!!)
        assertNotNull(ingredientUsageFound)
        val ingredientUsageId = ingredientUsageFound!!.ingredientUsageId

        // when
        ingredientService.deleteIngredientUsageById(ingredientUsageId, TestObjects.TEST_USER_ID)

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
        val recipeCreationResult =
            recipeService.createRecipe("Toller Kartoffelsalat", TestObjects.TEST_USER_ID)
        assertEquals(ActionResultStatus.CREATED, recipeCreationResult.status)
        assertNotNull(recipeCreationResult.id)
        val recipeId = recipeCreationResult.id!!

        val existingIngredient = ingredientService.findIngredientByName(ingredientName)!!
        val existingIngredientUnit =
            ingredientService.findIngredientUnitByName(ingredientUnitName)!!

        val ingredientUnitCreationResult =
            ingredientService.createIngredientUnit("Neue Einheit!", TestObjects.TEST_USER_ID)
        assertEquals(ActionResultStatus.CREATED, ingredientUnitCreationResult.status)
        val newIngredientUnitId = ingredientUnitCreationResult.id!!

        val ingredientCreationResult =
            ingredientService.createIngredient("Neue Zutat!", TestObjects.TEST_USER_ID)
        assertEquals(ActionResultStatus.CREATED, ingredientCreationResult.status)
        val newIngredientId = ingredientCreationResult.id!!

        val newAmount = ingredientUsageAmount + 2

        val creationResult = ingredientService.createIngredientUsage(
            recipeId,
            ingredientId = existingIngredient.ingredientId,
            ingredientUnitId = existingIngredientUnit.ingredientUnitId,
            amount = ingredientUsageAmount,
            userId = TestObjects.TEST_USER_ID,
        )
        val ingredientUsageFound = ingredientService.getIngredientUsageById(creationResult.id!!)
        assertNotNull(ingredientUsageFound)

        // when
        val updateResult = ingredientService.updateIngredientUsage(
            ingredientUsageId = ingredientUsageFound!!.ingredientUsageId,
            ingredientId = newIngredientId,
            ingredientUnitId = newIngredientUnitId,
            amount = newAmount,
            userId = TestObjects.TEST_USER_ID
        )

        // expect
        assertEquals(ActionResultStatus.UPDATED, updateResult.status)
        assertEquals(ingredientUsageFound.ingredientUsageId, updateResult.id)
        val updatedIngredientUsage =
            ingredientService.getIngredientUsageById(ingredientUsageFound.ingredientUsageId)
        assertNotNull(updatedIngredientUsage!!)
        assertEquals(newIngredientId, updatedIngredientUsage.ingredientId)
        assertEquals(newIngredientUnitId, updatedIngredientUsage.ingredientUnitId)
        assertEquals(newAmount, updatedIngredientUsage.amount)
    }

    @Test
    @Order(12)
    fun `can not update ingredient of an ingredientUsage to an ingredient that already exists on recipe`() {
        // given
        val ingredientUsageAmount = 2.0
        val recipeCreationResult =
            recipeService.createRecipe("Tollster Kartoffelsalat", TestObjects.TEST_USER_ID)
        assertEquals(ActionResultStatus.CREATED, recipeCreationResult.status)
        assertNotNull(recipeCreationResult.id)
        val recipeId = recipeCreationResult.id!!

        val existingIngredient = ingredientService.findIngredientByName(ingredientName)!!
        val existingIngredientUnit =
            ingredientService.findIngredientUnitByName(ingredientUnitName)!!

        val ingredientUnitCreationResult =
            ingredientService.createIngredientUnit("Neueste Einheit!", TestObjects.TEST_USER_ID)
        assertEquals(ActionResultStatus.CREATED, ingredientUnitCreationResult.status)
        val newIngredientUnitId = ingredientUnitCreationResult.id!!

        val ingredientCreationResult =
            ingredientService.createIngredient("Neueste Zutat!", TestObjects.TEST_USER_ID)
        assertEquals(ActionResultStatus.CREATED, ingredientCreationResult.status)
        val newIngredientId = ingredientCreationResult.id!!

        val newAmount = ingredientUsageAmount + 2

        val creationResultOne = ingredientService.createIngredientUsage(
            recipeId,
            ingredientId = existingIngredient.ingredientId,
            ingredientUnitId = existingIngredientUnit.ingredientUnitId,
            amount = ingredientUsageAmount,
            userId = TestObjects.TEST_USER_ID,
        )
        val ingredientUsageOne = ingredientService.getIngredientUsageById(creationResultOne.id!!)
        assertNotNull(ingredientUsageOne!!)

        val creationResultTwo = ingredientService.createIngredientUsage(
            recipeId,
            ingredientId = newIngredientId,
            ingredientUnitId = newIngredientUnitId,
            amount = newAmount,
            userId = TestObjects.TEST_USER_ID,
        )
        assertNotNull(creationResultTwo)

        val ingredientUsageTwo = ingredientService.getIngredientUsageById(creationResultTwo.id!!)!!

        // when
        val updateResult = ingredientService.updateIngredientUsage(
            ingredientUsageId = ingredientUsageOne.ingredientUsageId,
            ingredientId = ingredientUsageTwo.ingredientId,
            ingredientUnitId = ingredientUsageOne.ingredientUnitId,
            amount = ingredientUsageOne.amount,
            userId = TestObjects.TEST_USER_ID,
        )

        // expect
        assertEquals(ActionResultStatus.INVALID_ARGUMENTS, updateResult.status)
        assertNull(updateResult.id)
        val updatedIngredientUsage =
            ingredientService.getIngredientUsageById(ingredientUsageOne.ingredientUsageId)
        assertNotNull(updatedIngredientUsage!!)
        assertEquals(ingredientUsageOne.ingredientId, updatedIngredientUsage.ingredientId)
        assertEquals(ingredientUsageOne.ingredientUnitId, updatedIngredientUsage.ingredientUnitId)
        assertEquals(ingredientUsageOne.amount, updatedIngredientUsage.amount)
    }

    @Test
    @Order(13)
    fun `deleting a recipe also deletes the connected ingredientUsages`() {
        // given
        val ingredientUsageAmount = 2.0
        val recipeCreationResult =
            recipeService.createRecipe("Eindeutiger Kartoffelsalat", TestObjects.TEST_USER_ID)
        assertEquals(ActionResultStatus.CREATED, recipeCreationResult.status)
        assertNotNull(recipeCreationResult.id)
        val recipeId = recipeCreationResult.id!!

        val existingIngredient = ingredientService.findIngredientByName(ingredientName)!!

        val existingIngredientUnit =
            ingredientService.findIngredientUnitByName(ingredientUnitName)!!

        val creationResult = ingredientService.createIngredientUsage(
            recipeId,
            ingredientId = existingIngredient.ingredientId,
            ingredientUnitId = existingIngredientUnit.ingredientUnitId,
            amount = ingredientUsageAmount,
            userId = TestObjects.TEST_USER_ID
        )
        val createdIngredientId = creationResult.id!!
        val ingredientUsageFound = ingredientService.getIngredientUsageById(createdIngredientId)
        assertNotNull(ingredientUsageFound)

        // when
        val deletionResult = recipeService.deleteRecipeById(recipeId, TestObjects.TEST_USER_ID)

        // expect
        assertEquals(ActionResultStatus.DELETED, deletionResult.status)
        assertEquals(recipeId, deletionResult.id)
        val deletedIngredientUsageFinding =
            ingredientService.getIngredientUsageById(createdIngredientId)
        assertNull(deletedIngredientUsageFinding)
    }

    @Test
    fun `deleted ingredientUnit is not found again`() {
        // given
        val ingredientUnitToSave = "Teelöffel"
        val creationResult =
            ingredientService.createIngredientUnit(ingredientUnitToSave, TestObjects.TEST_USER_ID)
        assertEquals(ActionResultStatus.CREATED, creationResult.status)
        assertNotNull(creationResult.id)
        val ingredientUnitId = creationResult.id!!
        val ingredientUnitFound = ingredientService.getIngredientUnitById(ingredientUnitId)!!
        assertEquals(ingredientUnitToSave, ingredientUnitFound.name)

        // when
        val deletionResult =
            ingredientService.deleteIngredientUnitById(ingredientUnitId, TestObjects.TEST_USER_ID)

        // expect
        assertEquals(ActionResultStatus.DELETED, deletionResult.status);
        val ingredientUnitFoundAgain = ingredientService.getIngredientUnitById(ingredientUnitId)
        assertNull(ingredientUnitFoundAgain)
    }

    @Test
    fun `deleting an unknown ingredientUnit returns ELEMENT_NOT_FOUND`() {
        // given
        val unknownIngredientUnitId = "unknown_ingredient_unit_id"

        // when
        val deletionResult = ingredientService.deleteIngredientUnitById(
            unknownIngredientUnitId,
            TestObjects.TEST_USER_ID
        )

        // expect
        assertEquals(ActionResultStatus.ELEMENT_NOT_FOUND, deletionResult.status)
    }

    @Test
    fun `deleted ingredient is not found again`() {
        // given
        val ingredientToSave = "Pflaumenmarmelade"
        val creationResult =
            ingredientService.createIngredient(ingredientToSave, TestObjects.TEST_USER_ID)
        assertEquals(ActionResultStatus.CREATED, creationResult.status)
        assertNotNull(creationResult.id)
        val ingredientId = creationResult.id!!
        val ingredientFound = ingredientService.getIngredientById(ingredientId)!!
        assertEquals(ingredientToSave, ingredientFound.name)

        // when
        val deletionResult =
            ingredientService.deleteIngredientById(ingredientId, TestObjects.TEST_USER_ID)

        // expect
        assertEquals(ActionResultStatus.DELETED, deletionResult.status);
        val ingredientFoundAgain = ingredientService.getIngredientById(ingredientId)
        assertNull(ingredientFoundAgain)
    }

    @Test
    fun `deleting an unknown ingredient returns ELEMENT_NOT_FOUND`() {
        // given
        val unknownIngredientId = "unknown_ingredient_id"

        // when
        val deletionResult =
            ingredientService.deleteIngredientById(unknownIngredientId, TestObjects.TEST_USER_ID)

        // expect
        assertEquals(ActionResultStatus.ELEMENT_NOT_FOUND, deletionResult.status)
    }

    @Test
    fun `deleting an unknown ingredientUsage returns ELEMENT_NOT_FOUND`() {
        // given
        val unknownIngredientUsageId = "unknown_ingredient_usage_id"

        // when
        val deletionResult = ingredientService.deleteIngredientUsageById(
            unknownIngredientUsageId,
            TestObjects.TEST_USER_ID
        )

        // expect
        assertEquals(ActionResultStatus.ELEMENT_NOT_FOUND, deletionResult.status)
    }
}
