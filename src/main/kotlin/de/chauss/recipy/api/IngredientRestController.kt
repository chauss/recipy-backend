package de.chauss.recipy.api;

import de.chauss.recipy.service.IngredientService
import org.springframework.beans.factory.annotation.Autowired
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
    fun createIngredientUnit(@RequestBody request: CreateIngredientUnitRequest): ResponseEntity<ActionResponse> {
        val result = ingredientService.createIngredientUnit(request.name)
        return ActionResponse.responseEntityForResult(result = result)
    }

    // ########################################################################
    // # Ingredient
    // ########################################################################
    @GetMapping("/ingredients")
    fun getAllIngredients() = ingredientService.getAllIngredients()

    @PostMapping("/ingredient", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createIngredient(@RequestBody request: CreateIngredientRequest): ResponseEntity<ActionResponse> {
        val result = ingredientService.createIngredient(request.name)
        return ActionResponse.responseEntityForResult(result = result)
    }

    // ########################################################################
    // # Ingredient Usage
    // ########################################################################
    @GetMapping("/ingredient/usages")
    fun getAllIngredientUsages(@RequestParam recipeId: String) =
        ingredientService.getAllIngredientUsagesForRecipe(recipeId)

    @PostMapping("/ingredient/usage", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createIngredientUsage(@RequestBody request: CreateIngredientUsageRequest): ResponseEntity<ActionResponse> {
        val result = ingredientService.createIngredientUsage(
            recipeId = request.recipeId,
            ingredientId = request.ingredientId,
            ingredientUnitId = request.ingredientUnitId,
            amount = request.amount
        )
        return ActionResponse.responseEntityForResult(result = result)
    }

    @PutMapping(
        "/ingredient/usage/{ingredientUsageId}",
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createIngredientUsage(
        @RequestBody request: UpdateIngredientUsageRequest,
        @PathVariable(value = "ingredientUsageId") ingredientUsageId: String
    ): ResponseEntity<ActionResponse> {
        val result = ingredientService.updateIngredientUsage(
            ingredientUsageId = ingredientUsageId,
            ingredientId = request.ingredientId,
            ingredientUnitId = request.ingredientUnitId,
            amount = request.amount
        )
        return ActionResponse.responseEntityForResult(result = result)
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

class UpdateIngredientUsageRequest(
    val ingredientId: String,
    val ingredientUnitId: String,
    val amount: Double,
)
