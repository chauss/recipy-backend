package de.chauss.recipy.service.dtos

import de.chauss.recipy.database.models.Ingredient
import java.time.Instant

class IngredientDto(
    val ingredientId: String,
    val name: String,
    val created: Instant
) {
    companion object {
        fun from(ingredient: Ingredient) =
            IngredientDto(
                ingredientId = ingredient.ingredientId,
                name = ingredient.name,
                created = ingredient.created
            )
    }
}