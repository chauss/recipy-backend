package de.chauss.recipy.api

import de.chauss.recipy.service.RecipeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/v1"])
class RecipeRestController(
    @Autowired val recipeService: RecipeService
) {
    @GetMapping("/recipes")
    fun getAllRecipes() = recipeService.getAllRecipes()

    @PostMapping("/recipe", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createRecipe(@RequestBody request: CreateRecipeRequest): ResponseEntity<CreationResponse> {
        val result = recipeService.createRecipe(request.name)
        return CreationResponse.responseEntityForResult(result = result)
    }
}

class CreateRecipeRequest(
    val name: String
)