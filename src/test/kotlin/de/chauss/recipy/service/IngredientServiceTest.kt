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
    val ingredientUnitName = "TL"
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
        assertEquals(result.status, CreationResultStatus.CREATED)
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
        assertEquals(result.status, CreationResultStatus.CREATED)
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
        assertEquals(result.status, CreationResultStatus.CREATED)
        assertNotNull(result.id)
        val ingredientUsageFound = ingredientService.getIngredientUsageById(result.id!!)
        assertEquals(ingredientUsageFound!!.ingredientUsageId, result.id)
        assertEquals(ingredientUsageFound.ingredientId, existingIngredient.ingredientId)
        assertEquals(ingredientUsageFound.ingredientUnitId, existingIngredientUnit.ingredientUnitId)
        assertEquals(ingredientUsageFound.amount, ingredientUsageAmount)
    }

    @Test
    @Order(5)
    fun `recipe can't have two ingredientUsages for the same ingredientId`() {
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
        assertEquals(successResult.status, CreationResultStatus.CREATED)
        assertNotNull(successResult.id)
        assertEquals(failureResult.status, CreationResultStatus.INVALID_ARGUMENTS)
        assertNull(failureResult.id)
        val ingredientUsageFound = ingredientService.getIngredientUsageById(successResult.id!!)!!
        assertEquals(ingredientUsageFound.ingredientUsageId, successResult.id)
        assertEquals(ingredientUsageFound.ingredientId, existingIngredient.ingredientId)
        assertEquals(ingredientUsageFound.ingredientUnitId, existingIngredientUnit.ingredientUnitId)
        assertEquals(ingredientUsageFound.amount, ingredientUsageAmount)
    }

    @Test
    @Order(6)
    fun `can not create two ingredients with the same name`() {
        // when
        val creationResult = ingredientService.createIngredient(ingredientName)

        // expect
        assertEquals(creationResult.status, CreationResultStatus.ALREADY_EXISTS)
        assertNotEquals(creationResult.message, "")
        assertNull(creationResult.id)
    }

    @Test
    @Order(7)
    fun `can not create two ingredientsUnits with the same name`() {
        // when
        val creationResult = ingredientService.createIngredientUnit(ingredientUnitName)

        // expect
        assertEquals(creationResult.status, CreationResultStatus.ALREADY_EXISTS)
        assertNotEquals(creationResult.message, "")
        assertNull(creationResult.id)
    }
}
