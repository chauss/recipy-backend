package de.chauss.recipy.api

import de.chauss.recipy.config.UserAuthTokenVerifier.AuthenticatedUser
import de.chauss.recipy.service.RecipeService
import de.chauss.recipy.service.dtos.RecipeDto
import de.chauss.recipy.service.dtos.RecipeImageDto
import de.chauss.recipy.service.dtos.RecipeOverviewDto
import org.apache.commons.io.FilenameUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
        return recipeService.getAllRecipesAsOverview(null)
    }

    @GetMapping("/recipes/overview/userId/{userId}")
    fun getAllRecipesForUserAsOverview(@PathVariable(value = "userId") userId: String): List<RecipeOverviewDto> {
        return recipeService.getAllRecipesAsOverview(forUserId = userId)
    }

    @PostMapping("/recipe", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createRecipe(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @RequestBody request: CreateRecipeRequest,
    ): ResponseEntity<ActionResponse> {
        val result = recipeService.createRecipe(request.name, user.userId)
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
    fun deleteRecipeById(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable(value = "recipeId") recipeId: String,
    ): ResponseEntity<ActionResponse> {
        val result = recipeService.deleteRecipeById(recipeId = recipeId, userId = user.userId)
        return ActionResponse.responseEntityForResult(result = result)
    }

    // ########################################################################
    // # Preparation steps
    // ########################################################################
    @PostMapping("/recipe/preparationStep", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun addPreparationStepToRecipe(
        @RequestBody request: CreatePreparationStepRequest,
        @AuthenticationPrincipal user: AuthenticatedUser,
    ): ResponseEntity<ActionResponse> {
        val result = recipeService.createPreparationStep(
            recipeId = request.recipeId,
            stepNumber = request.stepNumber,
            description = request.description,
            userId = user.userId,
        )
        return ActionResponse.responseEntityForResult(result = result)
    }

    @DeleteMapping("/recipe/preparationStep/{preparationStepId}")
    fun deletePreparationStepById(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable(value = "preparationStepId") preparationStepId: String,
    ): ResponseEntity<ActionResponse> {
        val result = recipeService.deletePreparationStepById(
            preparationStepId = preparationStepId,
            userId = user.userId,
        )
        return ActionResponse.responseEntityForResult(result = result)
    }

    @PutMapping(
        "/recipe/preparationStep/{preparationStepId}",
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun updatePreparationStep(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable(value = "preparationStepId") preparationStepId: String,
        @RequestBody request: UpdatePreparationStepRequest,
    ): ResponseEntity<ActionResponse> {
        val result = recipeService.updatePreparationStep(
            preparationStepId = preparationStepId,
            stepNumber = request.stepNumber,
            description = request.description,
            userId = user.userId,
        )
        return ActionResponse.responseEntityForResult(result = result)
    }

    // ########################################################################
    // # Recipe Images
    // ########################################################################
    @PostMapping("/recipe/image", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun addImageToRecipe(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @ModelAttribute request: AddImageToRecipeRequest,
    ): ResponseEntity<ActionResponse> {
        val fileExtension = FilenameUtils.getExtension(request.image.originalFilename)
        val result = recipeService.addImageToRecipe(
            recipeId = request.recipeId,
            imageData = request.image.bytes,
            fileExtension = fileExtension,
            userId = user.userId,
        )
        return ActionResponse.responseEntityForResult(result = result)
    }

    @DeleteMapping("/recipe/{recipeId}/image/{imageId}")
    fun deleteRecipeImageById(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable(value = "recipeId") recipeId: String,
        @PathVariable(value = "imageId") imageId: String,
    ): ResponseEntity<ActionResponse> {
        val result = recipeService.deleteRecipeImageById(
            recipeId = recipeId,
            imageId = imageId,
            userId = user.userId,
        )
        return ActionResponse.responseEntityForResult(result = result)
    }

    @GetMapping("/recipe/{recipeId}/image/{imageId}")
    fun getRecipeImageById(
        @PathVariable(value = "recipeId") recipeId: String,
        @PathVariable(value = "imageId") imageId: String,
    ): ResponseEntity<Any> {
        val result = recipeService.getRecipeImageById(recipeId = recipeId, imageId = imageId)
        return if (result == null) {
            ResponseEntity.notFound().build()
        } else {
            // TODO maybe add mediatype (jpeg, png, etc.) to response
            ResponseEntity.ok().body(result)
        }
    }

    @GetMapping("/recipe/{recipeId}/images")
    fun getRecipeImagesForRecipeId(
        @PathVariable(value = "recipeId") recipeId: String,
    ): Collection<RecipeImageDto> {
        return recipeService.getRecipeImagesForRecipeId(recipeId = recipeId)
    }
}

class CreateRecipeRequest(
    val name: String,
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
)
