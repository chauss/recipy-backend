package de.chauss.recipy.api

import de.chauss.recipy.service.RecipeService
import de.chauss.recipy.service.dtos.RecipeDto
import de.chauss.recipy.service.dtos.RecipeOverviewDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException

@RestController
@CrossOrigin("*")
@RequestMapping(value = ["/api/v1"])
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
    fun addPreparationStepToRecipe(@RequestBody request: CreatePreparationStepRequest): ResponseEntity<ActionResponse> {
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
    fun updatePreparationStep(
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

    // ########################################################################
    // # Recipe Images
    // ########################################################################
    @PostMapping("/recipe/image", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun addImageToRecipe(@ModelAttribute request: AddImageToRecipeRequest): ResponseEntity<ActionResponse> {
        val result = recipeService.addImageToRecipe(
            recipeId = request.recipeId,
            imageData = request.image.bytes,
            fileExtension = request.fileExtension,
        )
        return ActionResponse.responseEntityForResult(result = result)
    }

    @DeleteMapping("/recipe/{recipeId}/image/{imageId}")
    fun deleteRecipeImageById(
        @PathVariable(value = "recipeId") recipeId: String,
        @PathVariable(value = "imageId") imageId: String
    ): ResponseEntity<ActionResponse> {
        val result = recipeService.deleteRecipeImageById(recipeId = recipeId, imageId = imageId)
        return ActionResponse.responseEntityForResult(result = result)
    }

    @GetMapping("/recipe/{recipeId}/image/{imageId}")
    fun getRecipeImageById(
        @PathVariable(value = "recipeId") recipeId: String,
        @PathVariable(value = "imageId") imageId: String
    ): ResponseEntity<Any> {
        val result = recipeService.getRecipeImageById(recipeId = recipeId, imageId = imageId)
        return if (result == null) {
            ResponseEntity.notFound().build()
        } else {
            // TODO maybe add mediatype (jpeg, png, etc.) to response
            ResponseEntity.ok().body(result)
        }
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

class AddImageToRecipeRequest(
    val recipeId: String,
    val image: MultipartFile,
    val fileExtension: String,
)