package de.chauss.recipy.service

import de.chauss.recipy.database.models.*
import de.chauss.recipy.service.dtos.IngredientDto
import de.chauss.recipy.service.dtos.IngredientUnitDto
import de.chauss.recipy.service.dtos.IngredientUsageDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class IngredientService(
    @Autowired val ingredientUsageRepository: IngredientUsageRepository,
    @Autowired val ingredientUnitRepository: IngredientUnitRepository,
    @Autowired val ingredientRepository: IngredientRepository,
    @Autowired val recipeRepository: RecipeRepository
) {
    // ########################################################################
    // # Ingredient Unit
    // ########################################################################
    fun createIngredientUnit(name: String): ActionResult {
        val existingIngredientUnits = ingredientUnitRepository.findByNameIgnoreCase(name)

        if (existingIngredientUnits != null) {
            return ActionResult(
                status = ActionResultStatus.ALREADY_EXISTS,
                message = "ERROR: An ingredientUnit with the name \"$name\" already exists."
            )
        }
        val newIngredientUnit = IngredientUnit(name = name)
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
        val ingredientUnit = ingredientUnitRepository.findByNameIgnoreCase(ingredientUnitName)
        return ingredientUnit?.let { IngredientUnitDto.from(ingredientUnit = it) }
    }

    fun getAllIngredientUnits(): List<IngredientUnitDto> {
        val ingredientUnits = ingredientUnitRepository.findAll()

        return ingredientUnits.map { IngredientUnitDto.from(ingredientUnit = it) }
    }

    // ########################################################################
    // # Ingredient
    // ########################################################################
    fun createIngredient(name: String): ActionResult {
        val existingIngredient = ingredientRepository.findByNameIgnoreCase(name)

        if (existingIngredient != null) {
            return ActionResult(
                status = ActionResultStatus.ALREADY_EXISTS,
                message = "ERROR: An ingredient with the name \"$name\" already exists."
            )
        }
        val newIngredient = Ingredient(name = name)
        ingredientRepository.save(newIngredient)

        return ActionResult(
            status = ActionResultStatus.CREATED, id = newIngredient.ingredientId
        )
    }

    fun findIngredientByName(name: String): IngredientDto? {
        val ingredient = ingredientRepository.findByNameIgnoreCase(name)

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

    // ########################################################################
    // # IngredientUsage
    // ########################################################################
    fun createIngredientUsage(
        recipeId: String, ingredientId: String, ingredientUnitId: String, amount: Double
    ): ActionResult {
        val ingredient =
            ingredientRepository.findById(ingredientId).orElse(null)
                ?: return ActionResult(
                    status = ActionResultStatus.INVALID_ARGUMENTS,
                    message = "ERROR: IngredientId \"$ingredientId\" does not exist"
                )

        val ingredientUnit =
            ingredientUnitRepository.findById(ingredientUnitId).orElse(null)
                ?: return ActionResult(
                    status = ActionResultStatus.INVALID_ARGUMENTS,
                    message = "ERROR: IngredientUnitId \"$ingredientUnitId\" does not exist"
                )

        val recipe =
            recipeRepository.findById(recipeId).orElse(null)
                ?: return ActionResult(
                    status = ActionResultStatus.INVALID_ARGUMENTS,
                    message = "ERROR: RecipeId \"$recipeId\" does not exist"
                )

        if (recipe.ingredientUsages.any { it.ingredient.ingredientId == ingredient.ingredientId }) {
            return ActionResult(
                status = ActionResultStatus.INVALID_ARGUMENTS,
                message = "ERROR: IngredientId \"${ingredient.name}\" does already exist in recipe \"${recipe.name}\""
            )
        }

        val newIngredientUsage = IngredientUsage(
            ingredient = ingredient,
            unit = ingredientUnit,
            recipe = recipe,
            amount = amount
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

        return ingredientUsages?.let {
            it.map { ingredientUsage -> IngredientUsageDto.from(ingredientUsage = ingredientUsage) }
        } ?: Collections.emptyList()
    }

    fun updateIngredientUsage(
        ingredientUsageId: String,
        ingredientId: String,
        ingredientUnitId: String,
        amount: Double
    ): ActionResult {
        val ingredientUsageOptional = ingredientUsageRepository.findById(ingredientUsageId)
        val ingredientOptional = ingredientRepository.findById(ingredientId)
        val ingredientUnitOptional = ingredientUnitRepository.findById(ingredientUnitId)

        return try {
            val ingredientUsage = ingredientUsageOptional.get()
            val ingredient = ingredientOptional.get()
            val ingredientUnit = ingredientUnitOptional.get()
            ingredientUsage.amount = amount
            ingredientUsage.ingredient = ingredient
            ingredientUsage.unit = ingredientUnit

            ingredientUsageRepository.save(ingredientUsage)

            ActionResult(status = ActionResultStatus.UPDATED, id = ingredientUsageId)
        } catch (e: NoSuchElementException) {
            ActionResult(
                status = ActionResultStatus.ELEMENT_NOT_FOUND,
                message = "ERROR: One of the given ids did not match any element"
            )
        }
    }
}
