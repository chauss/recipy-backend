package de.chauss.recipy.service.dtos

import de.chauss.recipy.database.models.Recipe

data class RecipeDto(
    val recipeId: String,
    val name: String,
    val ingredientUsages: Set<IngredientUsageDto>,
    val created: Long,
) {
    companion object {
        fun from(recipe: Recipe): RecipeDto {
            return RecipeDto(
                recipeId = recipe.recipeId,
                name = recipe.name,
                created = recipe.created.toEpochMilli(),
                ingredientUsages = recipe.ingredientUsages
                    .map { IngredientUsageDto.from(it) }
                    .toSet()
            )
        }
    }
}