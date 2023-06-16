package de.chauss.recipy.service

import de.chauss.recipy.database.models.*
import de.chauss.recipy.service.dtos.IngredientDto
import de.chauss.recipy.service.dtos.IngredientUnitDto
import de.chauss.recipy.service.dtos.IngredientUsageDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class IngredientService(
    @Autowired val ingredientUsageRepository: IngredientUsageRepository,
    @Autowired val ingredientUnitRepository: IngredientUnitRepository,
    @Autowired val ingredientRepository: IngredientRepository,
    @Autowired val recipeRepository: RecipeRepository,
) {
    // ########################################################################
    // # Ingredient Unit
    // ########################################################################
    fun createIngredientUnit(name: String, userId: String): ActionResult {
        val trimmedName = name.trim()
        val existingIngredientUnits = ingredientUnitRepository.findByNameIgnoreCase(trimmedName)

        existingIngredientUnits?.let {
            return ActionResult(
                status = ActionResultStatus.ALREADY_EXISTS,
                message = "ERROR: An ingredientUnit with the name \"$trimmedName\" already exists.",
                errorCode = ErrorCodes.CREATE_INGREDIENT_UNIT_INGREDIENT_UNIT_NAME_ALREADY_EXISTS.value
            )
        }
        val newIngredientUnit = IngredientUnit(name = trimmedName, creator = userId)
        ingredientUnitRepository.save(newIngredientUnit)

        return ActionResult(
            status = ActionResultStatus.CREATED, id = newIngredientUnit.ingredientUnitId
        )
    }

    fun getIngredientUnitById(id: String): IngredientUnitDto? {
        val ingredientUnit = ingredientUnitRepository.findById(id)

        if (ingredientUnit.isPresent) {
            return IngredientUnitDto.from(ingredientUnit = ingredientUnit.get())
        }
        return null
    }

    fun findIngredientUnitByName(ingredientUnitName: String): IngredientUnitDto? {
        val trimmedName = ingredientUnitName.trim()
        val ingredientUnit = ingredientUnitRepository.findByNameIgnoreCase(trimmedName)
        return ingredientUnit?.let { IngredientUnitDto.from(ingredientUnit = it) }
    }

    fun getAllIngredientUnits(): List<IngredientUnitDto> {
        val ingredientUnits = ingredientUnitRepository.findAll()

        return ingredientUnits.map { IngredientUnitDto.from(ingredientUnit = it) }
    }

    fun deleteIngredientUnitById(ingredientUnitId: String, userId: String): ActionResult {
        // TODO Think about how to use userId to protect this (ROLES maybe?)
        ingredientUnitRepository.findById(ingredientUnitId).orElse(null) ?: return ActionResult(
            status = ActionResultStatus.ELEMENT_NOT_FOUND,
            message = "INFO: Could not delete ingredientUnit with id $ingredientUnitId because no ingredientUnit with the given id exists",
            errorCode = ErrorCodes.DELETE_INGREDIENT_UNIT_INGREDIENT_UNIT_ID_NOT_FOUND.value
        )

        val existingUsages =
            ingredientUsageRepository.findByIngredientUnitIngredientUnitId(ingredientUnitId)
        if (existingUsages.isNotEmpty()) {
            return ActionResult(
                status = ActionResultStatus.FAILED_TO_DELETE,
                message = "ERROR: Could not delete ingredientUnit with id $ingredientUnitId because it still has usages",
                errorCode = ErrorCodes.DELETE_INGREDIENT_UNIT_INGREDIENT_UNIT_STILL_HAS_USAGES.value
            )
        }

        ingredientUnitRepository.deleteById(ingredientUnitId)
        ingredientUnitRepository.findById(ingredientUnitId).orElse(null) ?: return ActionResult(
            status = ActionResultStatus.DELETED,
            id = ingredientUnitId,
        )
        return ActionResult(
            status = ActionResultStatus.FAILED_TO_DELETE,
            message = "ERROR: Could not delete ingredientUnit with id $ingredientUnitId for unknown reason",
            errorCode = ErrorCodes.DELETE_INGREDIENT_UNIT_UNKNOWN_REASON.value
        )
    }

    // ########################################################################
    // # Ingredient
    // ########################################################################
    fun createIngredient(name: String, userId: String): ActionResult {
        val trimmedName = name.trim()
        val existingIngredient = ingredientRepository.findByNameIgnoreCase(trimmedName)

        existingIngredient?.let {
            return ActionResult(
                status = ActionResultStatus.ALREADY_EXISTS,
                message = "ERROR: An ingredient with the name \"$trimmedName\" already exists.",
                errorCode = ErrorCodes.CREATE_INGREDIENT_INGREDIENT_NAME_ALREADY_EXISTS.value
            )
        }
        val newIngredient = Ingredient(name = trimmedName, creator = userId)
        ingredientRepository.save(newIngredient)

        return ActionResult(
            status = ActionResultStatus.CREATED, id = newIngredient.ingredientId
        )
    }

    fun findIngredientByName(name: String): IngredientDto? {
        val trimmedName = name.trim()
        val ingredient = ingredientRepository.findByNameIgnoreCase(trimmedName)

        return ingredient?.let { IngredientDto.from(ingredient = it) }
    }

    fun getIngredientById(id: String): IngredientDto? {
        val ingredient = ingredientRepository.findById(id)

        if (ingredient.isPresent) {
            return IngredientDto.from(ingredient = ingredient.get())
        }
        return null
    }

    fun getAllIngredients(): List<IngredientDto> {
        val ingredients = ingredientRepository.findAll()

        return ingredients.map { IngredientDto.from(ingredient = it) }
    }

    fun deleteIngredientById(ingredientId: String, userId: String): ActionResult {
        // TODO Think about how to use userId to protect this (ROLES maybe?)
        ingredientRepository.findById(ingredientId).orElse(null) ?: return ActionResult(
            status = ActionResultStatus.ELEMENT_NOT_FOUND,
            message = "INFO: Could not delete ingredient with id $ingredientId because no ingredient with the given id exists",
            errorCode = ErrorCodes.DELETE_INGREDIENT_INGREDIENT_ID_NOT_FOUND.value
        )

        val existingUsages =
            ingredientUsageRepository.findByIngredientIngredientId(ingredientId)
        if (existingUsages.isNotEmpty()) {
            return ActionResult(
                status = ActionResultStatus.FAILED_TO_DELETE,
                message = "ERROR: Could not delete ingredient with id $ingredientId because it still has usages",
                errorCode = ErrorCodes.DELETE_INGREDIENT_INGREDIENT_STILL_HAS_USAGES.value
            )
        }

        ingredientRepository.deleteById(ingredientId)
        ingredientRepository.findById(ingredientId).orElse(null) ?: return ActionResult(
            status = ActionResultStatus.DELETED,
            id = ingredientId,
        )
        return ActionResult(
            status = ActionResultStatus.FAILED_TO_DELETE,
            message = "ERROR: Could not delete ingredient with id $ingredientId for unknown reason",
            errorCode = ErrorCodes.DELETE_INGREDIENT_UNKNOWN_REASON.value
        )
    }

    // ########################################################################
    // # IngredientUsage
    // ########################################################################
    fun createIngredientUsage(
        recipeId: String,
        ingredientId: String,
        ingredientUnitId: String,
        amount: Double,
        userId: String
    ): ActionResult {
        val recipe =
            recipeRepository.findById(recipeId).orElse(null)
                ?: return ActionResult(
                    status = ActionResultStatus.INVALID_ARGUMENTS,
                    message = "ERROR: RecipeId \"$recipeId\" does not exist",
                    errorCode = ErrorCodes.CREATE_INGREDIENT_USAGE_RECIPE_ID_DOES_NOT_EXIST.value
                )

        if (recipe.creator != userId) {
            return ActionResult(
                status = ActionResultStatus.UNAUTHORIZED,
                message = "INFO: Could not add ingredientUSage to recipe with id $recipeId because the user is not authorized to edit the recipe",
                errorCode = ErrorCodes.UPDATE_RECIPE_USER_IS_NOT_AUTHORIZED.value
            )
        }

        val ingredient =
            ingredientRepository.findById(ingredientId).orElse(null)
                ?: return ActionResult(
                    status = ActionResultStatus.INVALID_ARGUMENTS,
                    message = "ERROR: IngredientId \"$ingredientId\" does not exist",
                    errorCode = ErrorCodes.CREATE_INGREDIENT_USAGE_INGREDIENT_ID_DOES_NOT_EXIST.value
                )

        val ingredientUnit =
            ingredientUnitRepository.findById(ingredientUnitId).orElse(null)
                ?: return ActionResult(
                    status = ActionResultStatus.INVALID_ARGUMENTS,
                    message = "ERROR: IngredientUnitId \"$ingredientUnitId\" does not exist",
                    errorCode = ErrorCodes.CREATE_INGREDIENT_USAGE_INGREDIENT_UNIT_ID_DOES_NOT_EXIST.value
                )

        if (recipe.ingredientUsages.any { it.ingredient.ingredientId == ingredient.ingredientId }) {
            return ActionResult(
                status = ActionResultStatus.INVALID_ARGUMENTS,
                message = "ERROR: IngredientId \"${ingredient.name}\" does already exist in recipe \"${recipe.name}\"",
                errorCode = ErrorCodes.CREATE_INGREDIENT_USAGE_INGREDIENT_ALREADY_USED_IN_RECIPE.value
            )
        }

        val newIngredientUsage = IngredientUsage(
            ingredient = ingredient,
            ingredientUnit = ingredientUnit,
            recipe = recipe,
            amount = amount,
        )
        ingredientUsageRepository.save(newIngredientUsage)

        return ActionResult(
            status = ActionResultStatus.CREATED, id = newIngredientUsage.ingredientUsageId
        )
    }

    fun getIngredientUsageById(id: String): IngredientUsageDto? {
        val ingredientUsage = ingredientUsageRepository.findById(id)

        if (ingredientUsage.isPresent) {
            return IngredientUsageDto.from(ingredientUsage = ingredientUsage.get())
        }
        return null
    }

    fun getAllIngredientUsagesForRecipe(recipeId: String): List<IngredientUsageDto> {
        val ingredientUsages = ingredientUsageRepository.findByRecipeRecipeId(recipeId)

        return ingredientUsages.map { ingredientUsage -> IngredientUsageDto.from(ingredientUsage = ingredientUsage) }
    }

    fun updateIngredientUsage(
        ingredientUsageId: String,
        ingredientId: String,
        ingredientUnitId: String,
        amount: Double,
        userId: String,
    ): ActionResult {
        val ingredientUsage =
            ingredientUsageRepository.findById(ingredientUsageId).orElse(null)
                ?: return ActionResult(
                    status = ActionResultStatus.INVALID_ARGUMENTS,
                    message = "ERROR: IngredientUsageId \"$ingredientUsageId\" does not exist",
                    errorCode = ErrorCodes.UPDATE_INGREDIENT_USAGE_INGREDIENT_USAGE_ID_NOT_FOUND.value
                )

        val recipe =
            recipeRepository.findById(ingredientUsage.recipe.recipeId).orElse(null)
                ?: return ActionResult(
                    status = ActionResultStatus.INVALID_ARGUMENTS,
                    message = "ERROR: RecipeId \"${ingredientUsage.recipe.recipeId}\" does not exist",
                    errorCode = ErrorCodes.UPDATE_INGREDIENT_USAGE_RECIPE_ID_NOT_FOUND.value
                )

        if (recipe.creator != userId) {
            return ActionResult(
                status = ActionResultStatus.UNAUTHORIZED,
                message = "INFO: Could not update ingredientUsage of recipe with id ${recipe.recipeId} because the user is not authorized to edit the recipe",
                errorCode = ErrorCodes.UPDATE_RECIPE_USER_IS_NOT_AUTHORIZED.value
            )
        }

        val ingredient =
            ingredientRepository.findById(ingredientId).orElse(null)
                ?: return ActionResult(
                    status = ActionResultStatus.INVALID_ARGUMENTS,
                    message = "ERROR: IngredientId \"$ingredientId\" does not exist",
                    errorCode = ErrorCodes.UPDATE_INGREDIENT_USAGE_INGREDIENT_ID_NOT_FOUND.value
                )

        val ingredientUnit =
            ingredientUnitRepository.findById(ingredientUnitId).orElse(null)
                ?: return ActionResult(
                    status = ActionResultStatus.INVALID_ARGUMENTS,
                    message = "ERROR: IngredientUnitId \"$ingredientUnitId\" does not exist",
                    errorCode = ErrorCodes.UPDATE_INGREDIENT_USAGE_INGREDIENT_UNIT_ID_NOT_FOUND.value
                )

        if (recipe.ingredientUsages.any { it.ingredient.ingredientId == ingredient.ingredientId && it.ingredientUsageId != ingredientUsageId }) {
            return ActionResult(
                status = ActionResultStatus.INVALID_ARGUMENTS,
                message = "ERROR: IngredientId \"${ingredient.name}\" does already exist in recipe \"${recipe.name}\"",
                errorCode = ErrorCodes.UPDATE_INGREDIENT_USAGE_INGREDIENT_ALREADY_USED_IN_RECIPE.value
            )
        }

        ingredientUsage.amount = amount
        ingredientUsage.ingredient = ingredient
        ingredientUsage.ingredientUnit = ingredientUnit

        ingredientUsageRepository.save(ingredientUsage)
        return ActionResult(status = ActionResultStatus.UPDATED, id = ingredientUsageId)
    }

    fun deleteIngredientUsageById(ingredientUsageId: String, userId: String): ActionResult {
        val ingredientUsage = ingredientUsageRepository.findById(ingredientUsageId).orElse(null)
            ?: return ActionResult(
                status = ActionResultStatus.ELEMENT_NOT_FOUND,
                message = "INFO: Could not delete ingredientUsage with id $ingredientUsageId because no ingredientUsage with the given id exists",
                errorCode = ErrorCodes.DELETE_INGREDIENT_USAGE_INGREDIENT_USAGE_ID_NOT_FOUND.value
            )

        if (ingredientUsage.recipe.creator != userId) {
            return ActionResult(
                status = ActionResultStatus.UNAUTHORIZED,
                message = "INFO: Could not delete ingredientUsage of recipe with id ${ingredientUsage.recipe.recipeId} because the user is not authorized to edit the recipe",
                errorCode = ErrorCodes.UPDATE_RECIPE_USER_IS_NOT_AUTHORIZED.value
            )
        }

        ingredientUsageRepository.deleteById(ingredientUsageId)
        ingredientUsageRepository.findById(ingredientUsageId).orElse(null) ?: return ActionResult(
            status = ActionResultStatus.DELETED,
            id = ingredientUsageId,
        )
        return ActionResult(
            status = ActionResultStatus.FAILED_TO_DELETE,
            message = "ERROR: Could not delete ingredientUsage with id $ingredientUsageId for unknown reason",
            errorCode = ErrorCodes.DELETE_INGREDIENT_USAGE_UNKNOWN_REASON.value
        )
    }
}
