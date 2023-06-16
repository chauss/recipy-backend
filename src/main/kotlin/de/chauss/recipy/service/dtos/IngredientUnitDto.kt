package de.chauss.recipy.service.dtos

import de.chauss.recipy.database.models.IngredientUnit

class IngredientUnitDto(
    val ingredientUnitId: String,
    val name: String,
    val creator: String,
    val created: Long
) {
    companion object {
        fun from(ingredientUnit: IngredientUnit) =
            IngredientUnitDto(
                ingredientUnitId = ingredientUnit.ingredientUnitId,
                name = ingredientUnit.name,
                creator = ingredientUnit.creator,
                created = ingredientUnit.created.toEpochMilli()
            )
    }
}