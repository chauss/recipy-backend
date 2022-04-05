package de.chauss.recipy.service

import de.chauss.recipy.database.models.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class IngredientService(
    @Autowired val ingredientUsageRepository: IngredientUsageRepository,
    @Autowired val ingredientUnitRepository: IngredientUnitRepository,
    @Autowired val ingredientRepository: IngredientRepository,
    @Autowired val recipeService: RecipeService
) {
    // Ingredient Unit
    fun createIngredientUnit(name: String): CreationResult {
        val existingIngredientUnits = ingredientUnitRepository.findByName(name)

        if (existingIngredientUnits?.isNotEmpty() == true) {
            return CreationResult(status = CreationResultStatus.ALREADY_EXISTS)
        }
        val newIngredientUnit = IngredientUnit(name = name)
        ingredientUnitRepository.save(newIngredientUnit)

        return CreationResult(
            status = CreationResultStatus.CREATED, id = newIngredientUnit.ingredientUnitId
        )
    }

    fun getIngredientUnitById(id: String): IngredientUnit =
        ingredientUnitRepository.findById(id).get()

    fun findIngredientUnitByName(ingredientUnitName: String): List<IngredientUnit>? =
        ingredientUnitRepository.findByName(ingredientUnitName)

    // Ingredient
    fun createIngredient(name: String): CreationResult {
        val existingIngredients = ingredientRepository.findByName(name)

        if (existingIngredients?.isNotEmpty() == true) {
            return CreationResult(status = CreationResultStatus.ALREADY_EXISTS)
        }
        val newIngredient = Ingredient(name = name)
        ingredientRepository.save(newIngredient)

        return CreationResult(
            status = CreationResultStatus.CREATED, id = newIngredient.ingredientId
        )
    }

    fun findIngredientByName(name: String) = ingredientRepository.findByName(name)

    fun getIngredientById(id: String) = ingredientRepository.findById(id).get()

    // IngredientUsage
    fun createIngredientUsage(
        recipeId: String, ingredientId: String, ingredientUnitId: String, amount: Double
    ): CreationResult {
        val ingredient =
            ingredientRepository.findById(ingredientId).orElseGet(null)
                ?: return CreationResult(
                    status = CreationResultStatus.INVALID_ARGUMENTS,
                    message = "Given ingredientId does not exist"
                )

        val ingredientUnit =
            ingredientUnitRepository.findById(ingredientUnitId).orElseGet(null)
                ?: return CreationResult(
                    status = CreationResultStatus.INVALID_ARGUMENTS,
                    message = "Given ingredientUnitId does not exist"
                )

        val recipe =
            recipeService.getRecipeById(recipeId).orElseGet(null)
                ?: return CreationResult(
                    status = CreationResultStatus.INVALID_ARGUMENTS,
                    message = "Given recipeId does not exist"
                )

        if (recipe.ingredientUsages.any { it.ingredient.ingredientId == ingredient.ingredientId }) {
            return CreationResult(
                status = CreationResultStatus.INVALID_ARGUMENTS,
                message = "Given ingredient does already exist in given recipe"
            )
        }

        val newIngredientUsage = IngredientUsage(
            ingredient = ingredient,
            unit = ingredientUnit,
            recipe = recipe,
            amount = amount
        )
        ingredientUsageRepository.save(newIngredientUsage)
        recipeService.addIngredientUsage(recipeId, newIngredientUsage)

        return CreationResult(
            status = CreationResultStatus.CREATED, id = newIngredientUsage.ingredientUsageId
        )
    }

    fun getIngredientUsageById(id: String) = ingredientUsageRepository.findById(id).get()
}