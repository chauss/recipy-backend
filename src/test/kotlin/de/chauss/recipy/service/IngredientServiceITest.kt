package de.chauss.recipy.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

fun postgresForIngredients(imageName: String, opts: JdbcDatabaseContainer<Nothing>.() -> Unit) =
    PostgreSQLContainer<Nothing>(DockerImageName.parse(imageName)).apply(opts)

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Testcontainers
@Disabled("Can only run local (not in pipeline)")
@TestMethodOrder(OrderAnnotation::class)
class IngredientTest(
    @Autowired val ingredientService: IngredientService,
    @Autowired val recipeService: RecipeService,
) {
    val ingredientUnitName = "Prise"
    val ingredientName = "Olivenöl"

    companion object {
        @Container
        val postgresqlContainer = postgresForIngredients("postgres:14.2-alpine") {
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
    @Order(1)
    fun `container is up and running`() {
        assertTrue(postgresqlContainer.isRunning)
    }

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
        recipeService.deleteRecipeById(recipeId)

        // expect
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