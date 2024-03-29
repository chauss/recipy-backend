package de.chauss.recipy.service.dtos

import de.chauss.recipy.database.models.Ingredient

class IngredientDto(
    val ingredientId: String,
    val name: String,
    val creator: String,
    val created: Long
) {
    companion object {
        fun from(ingredient: Ingredient) =
            IngredientDto(
                ingredientId = ingredient.ingredientId,
                name = ingredient.name,
                creator = ingredient.creator,
                created = ingredient.created.toEpochMilli()
            )
    }
}