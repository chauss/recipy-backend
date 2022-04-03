package de.chauss.recipy.service

import de.chauss.recipy.database.CreationResult
import de.chauss.recipy.database.CreationResultStatus
import de.chauss.recipy.database.models.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class IngredientService(
    @Autowired val ingredientUsageRepository: IngredientUsageRepository,
    @Autowired val ingredientUnitRepository: IngredientUnitRepository,
    @Autowired val ingredientRepository: IngredientRepository
) {
    // Ingredient Unit
    fun createIngredientUnit(name: String): CreationResult {
        val existingIngredientUnits = ingredientUnitRepository.findByName(name)

        if (existingIngredientUnits?.isNotEmpty() == true) {
            return CreationResult(status = CreationResultStatus.ALREADY_EXISTS)
        }
        val newIngredientUnit = IngredientUnit(name = name)
        ingredientUnitRepository.save(newIngredientUnit)

        return CreationResult(status = CreationResultStatus.CREATED, id = newIngredientUnit.ingredientUnitId)
    }

    fun getIngredientUnitById(id: String): IngredientUnit =
        ingredientUnitRepository.findById(id).get()

    fun findIngredientUnitByName(ingredientUnitName: String): List<IngredientUnit?>? =
        ingredientUnitRepository.findByName(ingredientUnitName)

    // Ingredient
    fun createIngredient(name: String, ingredientUnit: IngredientUnit): CreationResult {
        val existingIngredients = ingredientRepository.findByName(name)

        if (existingIngredients?.isNotEmpty() == true) {
            val ingredientWithSameNameAndUnitExists = existingIngredients.any { ingredient -> ingredient.unit.ingredientUnitId == ingredientUnit.ingredientUnitId }
            if (ingredientWithSameNameAndUnitExists) {
                return CreationResult(status = CreationResultStatus.ALREADY_EXISTS)
            }
        }
        val newIngredient = Ingredient(name = name, unit = ingredientUnit)
        ingredientRepository.save(newIngredient)

        return CreationResult(status = CreationResultStatus.CREATED, id = newIngredient.ingredientId)
    }

    fun getIngredientById(id: String) = ingredientRepository.findById(id).get()
}