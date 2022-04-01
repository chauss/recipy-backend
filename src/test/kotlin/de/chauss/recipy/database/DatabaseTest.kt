package de.chauss.recipy.database

import de.chauss.recipy.database.models.Recipe
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID
import de.chauss.recipy.database.repositories.RecipeRepository
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DatabaseTest {

    @Autowired
    lateinit var recipeRepository: RecipeRepository

    @Test
    fun `save a recipe and find it again`() {
        // given
        val recipeId = UUID.randomUUID();
        val recipeToSave = Recipe(recipeId, "Kartoffelauflauf")

        // when
        recipeRepository.save(recipeToSave)
        val recipeFound = recipeRepository.findById(recipeId).get()

        // expect
        assertEquals(recipeToSave.name, recipeFound.name)
    }
}