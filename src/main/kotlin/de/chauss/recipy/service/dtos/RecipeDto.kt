package de.chauss.recipy.service.dtos

import de.chauss.recipy.database.models.Recipe
import java.time.Instant

data class RecipeDto(
    val recipeId: String,
    val name: String,
    val ingredientUsages: Set<IngredientUsageDto>,
    val created: Instant,
) {
    companion object {
        fun from(recipe: Recipe): RecipeDto {
            return RecipeDto(
                recipeId = recipe.recipeId,
                name = recipe.name,
                created = recipe.created,
                ingredientUsages = recipe.ingredientUsages
                    .map { IngredientUsageDto.from(it) }
                    .toSet()
            )
        }
    }
}