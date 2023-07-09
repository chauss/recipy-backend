package de.chauss.recipy.api

import de.chauss.recipy.config.UserAuthTokenVerifier.AuthenticatedUser
import de.chauss.recipy.service.RecipeService
import de.chauss.recipy.service.dtos.RecipeDto
import de.chauss.recipy.service.dtos.RecipeImageDto
import de.chauss.recipy.service.dtos.RecipeOverviewDto
import mu.KotlinLogging
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
    private val logger = KotlinLogging.logger {}

    // ########################################################################
    // # Recipes
    // ########################################################################
    @GetMapping("/recipes")
    fun getAllRecipes(): List<RecipeDto> {
        logger.debug { "Requesting all recipes..." }
        return recipeService.getAllRecipes()
    }

    @GetMapping("/recipes/overview")
    fun getAllRecipesAsOverview(): List<RecipeOverviewDto> {
        logger.debug { "Requesting all recipes as overview..." }
        return recipeService.getAllRecipesAsOverview(null)
    }

    @GetMapping("/recipes/overview/userId/{userId}")
    fun getAllRecipesForUserAsOverview(@PathVariable(value = "userId") userId: String): List<RecipeOverviewDto> {
        logger.debug { "Requesting all recipes as overview for userId=$userId..." }
        return recipeService.getAllRecipesAsOverview(forUserId = userId)
    }

    @PostMapping("/recipe", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createRecipe(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @RequestBody request: CreateRecipeRequest,
    ): ResponseEntity<ActionResponse> {
        logger.debug { "Creating recipe by userId=${user.userId} with recipe-name=${request.name}..." }
        val result = recipeService.createRecipe(request.name, user.userId)
        return ActionResponse.responseEntityForResult(result = result)
    }

    @GetMapping("/recipe/{recipeId}")
    fun getRecipeById(@PathVariable(value = "recipeId") recipeId: String): RecipeDto {
        logger.debug { "Requesting recipe with id=$recipeId" }
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
        logger.debug { "Deleting recipe with id=$recipeId. AuthenticatedUserId=${user.userId}..." }
        val result = recipeService.deleteRecipeById(recipeId = recipeId, userId = user.userId)
        return ActionResponse.responseEntityForResult(result = result)
    }

    // ########################################################################
    // # Preparation steps
    // ########################################################################
    @PostMapping("/recipe/preparationStep", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun addPreparationStepToRecipe(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @RequestBody request: CreatePreparationStepRequest,
    ): ResponseEntity<ActionResponse> {
        logger.debug { "Creating preparationStep on recipeId=${request.recipeId}. AuthenticatedUserId=${user.userId}..." }
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
        logger.debug { "Deleting preparationStep with id=$preparationStepId. AuthenticatedUserId=${user.userId}..." }
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
        logger.debug { "Updating preparationStep with id=$preparationStepId. AuthenticatedUserId=${user.userId}..." }
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
        logger.debug { "Adding recipeImage to recipe with id=${request.recipeId}. AuthenticatedUserId=${user.userId}..." }
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
        logger.debug { "Deleting recipeImage with Id=$imageId from recipe with id=$recipeId. AuthenticatedUserId=${user.userId}..." }
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
        logger.debug { "Requesting recipeImage with id=$imageId for recipe with id=$recipeId..." }
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
        logger.debug { "Requesting all recipeImages for recipe with id=$recipeId..." }
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
