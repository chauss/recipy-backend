package de.chauss.recipy.api

import de.chauss.recipy.database.models.Recipe
import de.chauss.recipy.database.models.RecipeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RecipeRestController(
    @Autowired val recipeRepository: RecipeRepository
    ) {
    @GetMapping("/recipes")
    fun getRecipe(): List<Recipe> {
        val recipes = recipeRepository.findAll()
        return recipes.toList()
    }

    @PostMapping("/recipe", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createRecipe(@RequestBody recipe: Recipe): HttpStatus {
        recipeRepository.save(recipe)
        return HttpStatus.CREATED
    }
}