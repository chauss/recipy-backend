package de.chauss.recipy.api

import de.chauss.recipy.service.RecipeService
import de.chauss.recipy.service.dtos.RecipeDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping(value = ["/v1"])
class RecipeRestController(
    @Autowired val recipeService: RecipeService
) {
    @GetMapping("/recipes")
    fun getAllRecipes() = recipeService.getAllRecipes()

    @PostMapping("/recipe", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createRecipe(@RequestBody request: CreateRecipeRequest): ResponseEntity<ActionResponse> {
        val result = recipeService.createRecipe(request.name)
        return ActionResponse.responseEntityForResult(result = result)
    }

    @GetMapping("/recipe/{recipeId}")
    fun getRecipeById(@PathVariable(value = "recipeId") recipeId: String): RecipeDto {
        val result = recipeService.getRecipeById(recipeId = recipeId)
        if (result != null) {
            return result
        }
        throw ResponseStatusException(HttpStatus.NOT_FOUND, "No recipe with the given id found")
    }
}

class CreateRecipeRequest(
    val name: String
)