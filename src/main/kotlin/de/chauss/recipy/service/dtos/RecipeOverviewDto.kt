package de.chauss.recipy.service.dtos

import de.chauss.recipy.database.models.Recipe

data class RecipeOverviewDto(
    val recipeId: String,
    val name: String,
    val creator: String,
    val created: Long,
) {
    companion object {
        fun from(recipe: Recipe): RecipeOverviewDto {
            return RecipeOverviewDto(
                recipeId = recipe.recipeId,
                name = recipe.name,
                creator = recipe.creator,
                created = recipe.created.toEpochMilli(),
            )
        }
    }
}