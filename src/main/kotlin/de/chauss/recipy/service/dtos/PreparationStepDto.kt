package de.chauss.recipy.service.dtos

import de.chauss.recipy.database.models.PreparationStep

data class PreparationStepDto(
    val preparationStepId: String,
    val recipeId: String,
    val stepNumber: Int,
    val description: String,
) {
    companion object {
        fun from(preparationStep: PreparationStep): PreparationStepDto {
            return PreparationStepDto(
                preparationStepId = preparationStep.preparationStepId,
                recipeId = preparationStep.recipe.recipeId,
                stepNumber = preparationStep.stepNumber,
                description = preparationStep.description,
            )
        }
    }
}