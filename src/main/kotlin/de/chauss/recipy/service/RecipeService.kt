package de.chauss.recipy.service

import de.chauss.recipy.database.models.Recipe
import de.chauss.recipy.database.models.RecipeRepository
import de.chauss.recipy.service.dtos.RecipeDto
import de.chauss.recipy.service.dtos.RecipeOverviewDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RecipeService(
    @Autowired val recipeRepository: RecipeRepository
) {
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
            return ActionResult(status = ActionResultStatus.ALREADY_EXISTS)
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
            message = "INFO: Could not delete recipe with id $recipeId because no recipe with the given id exists"
        )
        recipeRepository.deleteById(recipeId)
        recipeRepository.findById(recipeId).orElse(null) ?: return ActionResult(
            status = ActionResultStatus.DELETED,
            id = recipeId,
        )
        return ActionResult(
            status = ActionResultStatus.FAILED_TO_DELETE,
            message = "ERROR: Could not delete recipe with id $recipeId for unknown reason"
        )
    }
}