package de.chauss.recipy.database.models

import de.chauss.recipy.database.CreationResultStatus
import de.chauss.recipy.service.IngredientService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
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
    @Autowired val jdbc: JdbcTemplate
) {

    val ingredientUnitName = "TL"

    @AfterEach
    fun cleanup() {
        jdbc.execute("DELETE FROM ingredients WHERE true")
    }

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
    fun `container is up and running`() {
        assertTrue(postgresqlContainer.isRunning)
    }

    @Test
    @Order(1)
    fun `created ingredientUnit is found again`() {
        // when
        val result = ingredientService.createIngredientUnit(ingredientUnitName)
        assertEquals(result.status, CreationResultStatus.CREATED)
        assertNotNull(result.id)
        val ingredientUnitFound = ingredientService.getIngredientUnitById(result.id!!)

        // expect
        assertEquals(ingredientUnitFound.name, ingredientUnitName)
    }

    @Test
    @Order(2)
    fun `created ingredient is found again`() {
        // given
        val ingredientName = "Olivenöl"
        val existingIngredientUnits = ingredientService.findIngredientUnitByName(ingredientUnitName)
        assertNotNull(existingIngredientUnits)
        assertNotEquals(existingIngredientUnits!!.size, 0)
        val ingredientUnit = existingIngredientUnits[0]!!

        // when
        val result = ingredientService.createIngredient(ingredientName, ingredientUnit)
        assertEquals(result.status, CreationResultStatus.CREATED)
        assertNotNull(result.id)
        val ingredientFound = ingredientService.getIngredientById(result.id!!)

        // expect
        assertEquals(ingredientFound.name, ingredientName)
        assertEquals(ingredientFound.unit.ingredientUnitId, ingredientUnit.ingredientUnitId)
    }
}