package de.chauss.recipy.service.dtos

import de.chauss.recipy.database.models.IngredientUnit

class IngredientUnitDto(
    val ingredientUnitId: String,
    val name: String,
    val created: Long
) {
    companion object {
        fun from(ingredientUnit: IngredientUnit) =
            IngredientUnitDto(
                ingredientUnitId = ingredientUnit.ingredientUnitId,
                name = ingredientUnit.name,
                created = ingredientUnit.created.toEpochMilli()
            )
    }
}