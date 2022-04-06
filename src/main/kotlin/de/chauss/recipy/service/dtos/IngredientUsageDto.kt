package de.chauss.recipy.service.dtos

import de.chauss.recipy.database.models.IngredientUsage

class IngredientUsageDto(
    val ingredientUsageId: String,
    val ingredientId: String,
    val ingredientUnitId: String,
    val recipeId: String,
    val amount: Double,
    val created: Long
) {
    companion object {
        fun from(ingredientUsage: IngredientUsage): IngredientUsageDto =
            IngredientUsageDto(
                ingredientUsageId = ingredientUsage.ingredientUsageId,
                ingredientId = ingredientUsage.ingredient.ingredientId,
                ingredientUnitId = ingredientUsage.unit.ingredientUnitId,
                recipeId = ingredientUsage.recipe.recipeId,
                amount = ingredientUsage.amount,
                created = ingredientUsage.created.toEpochMilli(),
            )
    }
}
