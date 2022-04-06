package de.chauss.recipy.service

import de.chauss.recipy.database.models.Recipe
import de.chauss.recipy.database.models.RecipeRepository
import de.chauss.recipy.service.dtos.RecipeDto
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

    fun createRecipe(name: String): CreationResult {
        val existingRecipes = recipeRepository.findByName(name)

        if (existingRecipes?.isNotEmpty() == true) {
            return CreationResult(status = CreationResultStatus.ALREADY_EXISTS)
        }
        val newRecipe = Recipe(name = name)
        recipeRepository.save(newRecipe)

        return CreationResult(status = CreationResultStatus.CREATED, id = newRecipe.recipeId)
    }

    fun getRecipeById(recipeId: String): RecipeDto? {
        val recipe = recipeRepository.findById(recipeId)
        if (recipe.isPresent) {
            return RecipeDto.from(recipe.get())
        }
        return null
    }

    fun deleteRecipeById(recipeId: String): Boolean {
        recipeRepository.findById(recipeId).orElse(null) ?: return true
        recipeRepository.deleteById(recipeId)
        recipeRepository.findById(recipeId).orElse(null) ?: return true
        return false
    }
}