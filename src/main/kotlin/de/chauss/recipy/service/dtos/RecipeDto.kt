package de.chauss.recipy.service.dtos

import de.chauss.recipy.database.models.Recipe

data class RecipeDto(
    val recipeId: String,
    val name: String,
    val ingredientUsages: Set<IngredientUsageDto>,
    val preparationSteps: List<PreparationStepDto>,
    val recipeImages: List<RecipeImageDto>,
    val creator: String,
    val created: Long,
) {
    companion object {
        fun from(recipe: Recipe): RecipeDto {
            return RecipeDto(
                recipeId = recipe.recipeId,
                name = recipe.name,
                ingredientUsages = recipe.ingredientUsages
                    .map { IngredientUsageDto.from(it) }
                    .toSet(),
                preparationSteps = recipe.preparationSteps
                    .map { PreparationStepDto.from(it) },
                recipeImages = recipe.recipeImages
                    .map { RecipeImageDto.from(it) },
                creator = recipe.creator,
                created = recipe.created.toEpochMilli(),
            )
        }
    }
}