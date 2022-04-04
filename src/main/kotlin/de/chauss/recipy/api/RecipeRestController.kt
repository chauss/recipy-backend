package de.chauss.recipy.api

import de.chauss.recipy.database.CreationResultStatus
import de.chauss.recipy.database.models.Recipe
import de.chauss.recipy.service.RecipeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RecipeRestController(
    @Autowired val recipeService: RecipeService
    ) {
    @GetMapping("/recipes")
    fun getRecipe(): List<Recipe> = recipeService.getAllRecipes()

    @PostMapping("/recipe", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createRecipe(@RequestBody request: CreateRecipeRequest): HttpStatus {
        val result = recipeService.createRecipe(request.name)
        return if (result.status == CreationResultStatus.ALREADY_EXISTS) {
            HttpStatus.CONFLICT
        } else {
            HttpStatus.CREATED
        }
    }
}

class CreateRecipeRequest (
    val name: String
)