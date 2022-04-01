package de.chauss.recipy.api

import de.chauss.recipy.database.models.Recipe
import de.chauss.recipy.database.repositories.RecipeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RecipeRestController(
    @Autowired val recipeRepository: RecipeRepository
    ) {
    @GetMapping("/recipes")
    fun getRecipe(): List<Recipe> {
        println("Received recipe request!")
        val recipes = recipeRepository.findAll()

        recipes.forEach { recipe -> println(recipe) }

        return recipes.toList()
    }
}