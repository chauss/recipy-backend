package de.chauss.recipy.api

import de.chauss.recipy.service.RecipeService
import de.chauss.recipy.service.dtos.RecipeDto
import de.chauss.recipy.service.dtos.RecipeOverviewDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping(value = ["/v1"])
class RecipeRestController(
    @Autowired val recipeService: RecipeService,
) {
    // ########################################################################
    // # Recipes
    // ########################################################################
    @GetMapping("/recipes")
    fun getAllRecipes() = recipeService.getAllRecipes()

    @GetMapping("/recipes/overview")
    fun getAllRecipesAsOverview(): List<RecipeOverviewDto> {
        return recipeService.getAllRecipesAsOverview()
    }

    @PostMapping("/recipe", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createRecipe(@RequestBody request: CreateRecipeRequest): ResponseEntity<ActionResponse> {
        val result = recipeService.createRecipe(request.name)
        return ActionResponse.responseEntityForResult(result = result)
    }

    @GetMapping("/recipe/{recipeId}")
    fun getRecipeById(@PathVariable(value = "recipeId") recipeId: String): RecipeDto {
        val foundRecipe = recipeService.getRecipeById(recipeId = recipeId)
        if (foundRecipe != null) {
            return foundRecipe
        }
        throw ResponseStatusException(HttpStatus.NOT_FOUND, "No recipe with the given id found")
    }

    @DeleteMapping("/recipe/{recipeId}")
    fun deleteRecipeById(@PathVariable(value = "recipeId") recipeId: String): ResponseEntity<ActionResponse> {
        val result = recipeService.deleteRecipeById(recipeId = recipeId)
        return ActionResponse.responseEntityForResult(result = result)
    }

    // ########################################################################
    // # Preparation steps
    // ########################################################################
    @PostMapping("/recipe/preparationStep", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createRecipe(@RequestBody request: CreatePreparationStepRequest): ResponseEntity<ActionResponse> {
        val result = recipeService.createPreparationStep(
            recipeId = request.recipeId,
            stepNumber = request.stepNumber,
            description = request.description
        )
        return ActionResponse.responseEntityForResult(result = result)
    }

    @DeleteMapping("/recipe/preparationStep/{preparationStepId}")
    fun deletePreparationStepById(@PathVariable(value = "preparationStepId") preparationStepId: String): ResponseEntity<ActionResponse> {
        val result = recipeService.deletePreparationStepById(preparationStepId = preparationStepId)
        return ActionResponse.responseEntityForResult(result = result)
    }

    @PutMapping(
        "/recipe/preparationStep/{preparationStepId}",
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createIngredientUsage(
        @RequestBody request: UpdatePreparationStepRequest,
        @PathVariable(value = "preparationStepId") preparationStepId: String
    ): ResponseEntity<ActionResponse> {
        val result = recipeService.updatePreparationStep(
            preparationStepId = preparationStepId,
            stepNumber = request.stepNumber,
            description = request.description,
        )
        return ActionResponse.responseEntityForResult(result = result)
    }
}

class CreateRecipeRequest(
    val name: String
)

class CreatePreparationStepRequest(
    val recipeId: String,
    val stepNumber: Int,
    val description: String,
)

class UpdatePreparationStepRequest(
    val stepNumber: Int,
    val description: String,
)