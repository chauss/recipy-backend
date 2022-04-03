package de.chauss.recipy.service

import de.chauss.recipy.database.CreationResult
import de.chauss.recipy.database.CreationResultStatus
import de.chauss.recipy.database.models.Recipe
import de.chauss.recipy.database.models.RecipeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class RecipeService(
    @Autowired val recipeRepository: RecipeRepository
) {

    fun getAllRecipes(): List<Recipe> {
        val recipes = recipeRepository.findAll()
        return recipes.toList()
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

    fun getRecipeById(recipeId: String): Optional<Recipe> = recipeRepository.findById(recipeId)
}