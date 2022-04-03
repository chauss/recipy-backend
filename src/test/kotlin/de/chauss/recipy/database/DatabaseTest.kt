package de.chauss.recipy.database

import de.chauss.recipy.database.models.Recipe
import de.chauss.recipy.database.models.RecipeRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

fun postgres(imageName: String, opts: JdbcDatabaseContainer<Nothing>.() -> Unit) =
    PostgreSQLContainer<Nothing>(DockerImageName.parse(imageName)).apply(opts)

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Testcontainers
@Disabled("Can only run local (not in pipeline)")
class DatabaseTest(
    @Autowired val recipeRepository: RecipeRepository,
    @Autowired val jdbc: JdbcTemplate
) {

    @AfterEach
    fun cleanup() {
        jdbc.execute("DROP TABLE IF EXISTS recipe")
    }

    companion object {
        @Container
        val postgresqlContainer = postgres("postgres:14.2-alpine") {
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
        val recipeToSave = Recipe(name = "Kartoffelauflauf")
        val recipeId = recipeToSave.recipeId
        // when
        recipeRepository.save(recipeToSave)
        val recipeFound = recipeRepository.findById(recipeId).get()

        // expect
        assertEquals(recipeFound.name, recipeToSave.name)
        }
    }