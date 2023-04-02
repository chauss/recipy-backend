package de.chauss.recipy.service

import de.chauss.recipy.database.models.*
import de.chauss.recipy.service.dtos.PreparationStepDto
import de.chauss.recipy.service.dtos.RecipeDto
import de.chauss.recipy.service.dtos.RecipeImageDto
import de.chauss.recipy.service.dtos.RecipeOverviewDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class RecipeService(
    @Autowired val recipeRepository: RecipeRepository,
    @Autowired val preparationStepRepository: PreparationStepRepository,
    @Autowired val imageRepository: ImageRepository,
    @Autowired val recipeImageRepository: RecipeImageRepository
) {
    // ########################################################################
    // # Recipe
    // ########################################################################
    fun getAllRecipes(): List<RecipeDto> {
        val recipes = recipeRepository.findAll()
        return recipes.map { RecipeDto.from(it) }
    }

    fun getAllRecipesAsOverview(): List<RecipeOverviewDto> {
        val recipes = recipeRepository.findAll()
        return recipes.map { RecipeOverviewDto.from(it) }
    }

    fun createRecipe(name: String, userId: String): ActionResult {
        val trimmedName = name.trim()
        val existingRecipes = recipeRepository.findByName(trimmedName)

        if (existingRecipes?.isNotEmpty() == true) {
            return ActionResult(
                status = ActionResultStatus.ALREADY_EXISTS,
                message = "ERROR: A recipe with the name \"${name}\" does already exist",
                errorCode = ErrorCodes.CREATE_RECIPE_RECIPE_NAME_ALREADY_EXISTS.value
            )
        }
        val newRecipe = Recipe(name = trimmedName, creator = userId)
        recipeRepository.save(newRecipe)

        return ActionResult(status = ActionResultStatus.CREATED, id = newRecipe.recipeId)
    }

    fun getRecipeById(recipeId: String): RecipeDto? {
        val recipe = recipeRepository.findById(recipeId)
        if (recipe.isPresent) {
            return RecipeDto.from(recipe.get())
        }
        return null
    }

    fun deleteRecipeById(recipeId: String, userId: String): ActionResult {
        val recipe = recipeRepository.findById(recipeId).orElse(null) ?: return ActionResult(
            status = ActionResultStatus.UNAUTHORIZED,
            message = "INFO: Could not delete recipe with id $recipeId because no recipe with the given id exists",
            errorCode = ErrorCodes.DELETE_RECIPE_RECIPE_ID_NOT_FOUND.value
        )
        if (recipe.creator != userId) {
            return ActionResult(
                status = ActionResultStatus.ELEMENT_NOT_FOUND,
                message = "INFO: Could not delete recipe with id $recipeId because the user is not authorized to delete this recipe",
                errorCode = ErrorCodes.DELETE_RECIPE_USER_IS_NOT_AUTHORIZED.value
            )
        }
        recipeRepository.deleteById(recipeId)
        imageRepository.deleteAllImagesForRecipe(recipeId = recipeId)
        recipeRepository.findById(recipeId).orElse(null) ?: return ActionResult(
            status = ActionResultStatus.DELETED,
            id = recipeId,
        )
        return ActionResult(
            status = ActionResultStatus.FAILED_TO_DELETE,
            message = "ERROR: Could not delete recipe with id $recipeId for unknown reason",
            errorCode = ErrorCodes.DELETE_RECIPE_UNKNOWN_REASON.value
        )
    }

    // ########################################################################
    // # Preparation Step
    // ########################################################################
    fun getPreparationStepById(id: String): PreparationStepDto? {
        val preparationStep = preparationStepRepository.findById(id)

        if (preparationStep.isPresent) {
            return PreparationStepDto.from(preparationStep = preparationStep.get())
        }
        return null
    }

    fun createPreparationStep(
        recipeId: String,
        stepNumber: Int,
        description: String
    ): ActionResult {
        val recipeOptional = recipeRepository.findById(recipeId)
        if (recipeOptional.isEmpty) {
            return ActionResult(
                status = ActionResultStatus.INVALID_ARGUMENTS,
                message = "ERROR: Could not create preparation step for recipeId ($recipeId) because no recipe with the given id exists",
                errorCode = ErrorCodes.CREATE_PREPARATION_STEP_RECIPE_ID_DOES_NOT_EXIST.value
            )
        }
        val recipe = recipeOptional.get()

        val newPreparationStep = PreparationStep(
            recipe = recipe,
            stepNumber = stepNumber,
            description = description.trim(),
        )

        preparationStepRepository.save(newPreparationStep)

        return ActionResult(
            status = ActionResultStatus.CREATED,
            id = newPreparationStep.preparationStepId
        )
    }

    fun deletePreparationStepById(preparationStepId: String): ActionResult {
        preparationStepRepository.findById(preparationStepId).orElse(null) ?: return ActionResult(
            status = ActionResultStatus.ELEMENT_NOT_FOUND,
            message = "INFO: Could not delete preparationStep with id $preparationStepId because no preparationStep with the given id exists",
            errorCode = ErrorCodes.DELETE_PREPARATION_STEP_PREPARATION_STEP_ID_NOT_FOUND.value
        )
        preparationStepRepository.deleteById(preparationStepId)
        recipeRepository.findById(preparationStepId).orElse(null) ?: return ActionResult(
            status = ActionResultStatus.DELETED,
            id = preparationStepId,
        )
        return ActionResult(
            status = ActionResultStatus.FAILED_TO_DELETE,
            message = "ERROR: Could not delete preparationStep with id $preparationStepId for unknown reason",
            errorCode = ErrorCodes.DELETE_PREPARATION_STEP_UNKNOWN_REASON.value
        )
    }

    fun updatePreparationStep(
        preparationStepId: String,
        stepNumber: Int,
        description: String
    ): ActionResult {
        val preparationStep = preparationStepRepository.findById(preparationStepId).orElse(null)
            ?: return ActionResult(
                status = ActionResultStatus.ELEMENT_NOT_FOUND,
                message = "ERROR: Could not update preparationStep with id $preparationStepId because no preparationStep with the given id exists",
                errorCode = ErrorCodes.UPDATE_PREPARATION_STEP_PREPARATION_STEP_ID_NOT_FOUND.value
            )
        preparationStep.stepNumber = stepNumber
        preparationStep.description = description

        preparationStepRepository.save(preparationStep)

        return ActionResult(
            status = ActionResultStatus.UPDATED,
            id = preparationStep.preparationStepId
        )
    }

    fun addImageToRecipe(
        imageData: ByteArray,
        recipeId: String,
        fileExtension: String,
    ): ActionResult {
        val recipeOptional = recipeRepository.findById(recipeId)
        if (recipeOptional.isEmpty) {
            return ActionResult(
                status = ActionResultStatus.ELEMENT_NOT_FOUND,
                message = "ERROR: Could not add image to recipe because no recipe with the given id $recipeId exists.",
                errorCode = ErrorCodes.ADD_RECIPE_IMAGE_RECIPE_DOES_NOT_EXIST.value
            )
        }
        val recipe = recipeOptional.get()
        if (imageData.isEmpty()) {
            return ActionResult(
                status = ActionResultStatus.INVALID_ARGUMENTS,
                message = "ERROR: Could not add image to recipe because the given image data is empty.",
                errorCode = ErrorCodes.ADD_RECIPE_IMAGE_NO_IMAGE_UPLOADED.value
            )
        }

        val imageId: String
        try {
            imageId = imageRepository.saveImage(
                bytes = imageData,
                recipeId = recipeId,
                extension = fileExtension
            )
        } catch (e: Exception) {
            return ActionResult(
                status = ActionResultStatus.FAILED_TO_CREATE,
                message = "ERROR: Could not add image to recipe for an unknown reason: ${e.message}.",
                errorCode = ErrorCodes.ADD_RECIPE_IMAGE_UNKNOWN_REASON.value
            )
        }

        val newRecipeImage = RecipeImage(
            imageId = imageId,
            recipe = recipe,
            index = recipe.recipeImages.size,
        )

        recipeImageRepository.save(newRecipeImage)

        return ActionResult(
            status = ActionResultStatus.CREATED,
            id = imageId
        )
    }

    fun deleteRecipeImageById(recipeId: String, imageId: String): ActionResult {
        val recipeImageOptional = recipeImageRepository.findById(imageId)
        if (recipeImageOptional.isEmpty) {
            return ActionResult(
                status = ActionResultStatus.INVALID_ARGUMENTS,
                message = "ERROR: Could not delete image $imageId from recipe because no image with the given id exists",
                errorCode = ErrorCodes.DELETE_RECIPE_IMAGE_IMAGE_DOES_NOT_EXIST.value
            )
        }
        val recipeImage = recipeImageOptional.get()
        recipeImageRepository.delete(recipeImage)
        imageRepository.deleteImage(imageId = imageId, recipeId = recipeId)

        return ActionResult(
            status = ActionResultStatus.DELETED,
            id = imageId
        )
    }

    fun getRecipeImageById(recipeId: String, imageId: String): ByteArray? {
        return imageRepository.loadImage(imageId = imageId, recipeId = recipeId)
    }

    fun getRecipeImagesForRecipeId(recipeId: String): Collection<RecipeImageDto> {
        val recipeImages = recipeImageRepository.findByRecipeRecipeId(recipeId = recipeId)
        return recipeImages?.map { RecipeImageDto.from(it) } ?: Collections.emptyList()
    }
}