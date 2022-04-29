package de.chauss.recipy.service.dtos

import de.chauss.recipy.database.models.PreparationStep
import de.chauss.recipy.database.models.Recipe

data class PreparationStepDto(
    val preparationStepId: String,
    val recipe: Recipe,
    val stepNumber: Int,
    val description: String,
) {
    companion object {
        fun from(preparationStep: PreparationStep): PreparationStepDto {
            return PreparationStepDto(
                preparationStepId = preparationStep.preparationStepId,
                recipe = preparationStep.recipe,
                stepNumber = preparationStep.stepNumber,
                description = preparationStep.description,
            )
        }
    }
}