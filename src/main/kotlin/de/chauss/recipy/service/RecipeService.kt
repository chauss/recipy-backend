package de.chauss.recipy.service

import de.chauss.recipy.database.models.PreparationStep
import de.chauss.recipy.database.models.PreparationStepRepository
import de.chauss.recipy.database.models.Recipe
import de.chauss.recipy.database.models.RecipeRepository
import de.chauss.recipy.service.dtos.PreparationStepDto
import de.chauss.recipy.service.dtos.RecipeDto
import de.chauss.recipy.service.dtos.RecipeOverviewDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RecipeService(
    @Autowired val recipeRepository: RecipeRepository,
    @Autowired val preparationStepRepository: PreparationStepRepository,
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

    fun createRecipe(name: String): ActionResult {
        val trimmedName = name.trim()
        val existingRecipes = recipeRepository.findByName(trimmedName)

        if (existingRecipes?.isNotEmpty() == true) {
            return ActionResult(
                status = ActionResultStatus.ALREADY_EXISTS,
                message = "ERROR: A recipe with the name \"${name}\" does already exist",
                errorCode = ErrorCodes.CREATE_RECIPE_RECIPE_NAME_ALREADY_EXISTS.value
            )
        }
        val newRecipe = Recipe(name = trimmedName)
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

    fun deleteRecipeById(recipeId: String): ActionResult {
        recipeRepository.findById(recipeId).orElse(null) ?: return ActionResult(
            status = ActionResultStatus.ELEMENT_NOT_FOUND,
            message = "INFO: Could not delete recipe with id $recipeId because no recipe with the given id exists",
            errorCode = ErrorCodes.DELETE_RECIPE_RECIPE_ID_NOT_FOUND.value
        )
        recipeRepository.deleteById(recipeId)
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
}