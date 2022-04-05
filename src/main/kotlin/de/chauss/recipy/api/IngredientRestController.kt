package de.chauss.recipy.api;

import de.chauss.recipy.service.CreationResultStatus
import de.chauss.recipy.service.IngredientService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = ["/v1"])
class IngredientRestController(
    @Autowired val ingredientService: IngredientService,
) {
    // ########################################################################
    // # Ingredient Unit
    // ########################################################################
    @GetMapping("/ingredient/units")
    fun getAllIngredientUnits() = ingredientService.getAllIngredientUnits()

    @PostMapping("/ingredient/unit", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createIngredientUnit(@RequestBody request: CreateIngredientUnitRequest): ResponseEntity<CreationResponse> {
        val result = ingredientService.createIngredientUnit(request.name)
        return CreationResponse.responseEntityForResult(result = result)
    }

    // ########################################################################
    // # Ingredient
    // ########################################################################
    @GetMapping("/ingredients")
    fun getAllIngredients() = ingredientService.getAllIngredients()

    @PostMapping("/ingredient", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createIngredient(@RequestBody request: CreateIngredientRequest): ResponseEntity<CreationResponse> {
        val result = ingredientService.createIngredient(request.name)
        return CreationResponse.responseEntityForResult(result = result)
    }

    // ########################################################################
    // # Ingredient Usage
    // ########################################################################
    @GetMapping("/ingredient/usages")
    fun getAllIngredientUsages(@RequestParam recipeId: String) =
        ingredientService.getAllIngredientUsagesForRecipe(recipeId)

    @PostMapping("/ingredient/usage", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createIngredientUsage(@RequestBody request: CreateIngredientUsageRequest): ResponseEntity<CreationResponse> {
        val result = ingredientService.createIngredientUsage(
            recipeId = request.recipeId,
            ingredientId = request.ingredientId,
            ingredientUnitId = request.ingredientUnitId,
            amount = request.amount
        )
        return CreationResponse.responseEntityForResult(result = result)
    }
}

class CreateIngredientRequest(
    val name: String
)

class CreateIngredientUnitRequest(
    val name: String
)

class CreateIngredientUsageRequest(
    val recipeId: String,
    val ingredientId: String,
    val ingredientUnitId: String,
    val amount: Double,
)
