package de.chauss.recipy.service.dtos

import de.chauss.recipy.database.models.RecipeImage

class RecipeImageDto(
    val imageId: String,
    val recipeId: String,
    var index: Int,
    val created: Long,
) {
    companion object {
        fun from(recipeImage: RecipeImage): RecipeImageDto {
            return RecipeImageDto(
                imageId = recipeImage.imageId,
                recipeId = recipeImage.recipe.recipeId,
                index = recipeImage.index,
                created = recipeImage.created.toEpochMilli(),
            )
        }
    }
}