package de.chauss.recipy.api;

import de.chauss.recipy.config.UserAuthTokenVerifier
import de.chauss.recipy.service.IngredientService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin("*")
@RequestMapping(value = ["/api/v1"])
class IngredientRestController(
    @Autowired val ingredientService: IngredientService,
) {
    // ########################################################################
    // # Ingredient Unit
    // ########################################################################
    @GetMapping("/ingredient/units")
    fun getAllIngredientUnits() = ingredientService.getAllIngredientUnits()

    @PostMapping("/ingredient/unit", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createIngredientUnit(
        @AuthenticationPrincipal user: UserAuthTokenVerifier.AuthenticatedUser,
        @RequestBody request: CreateIngredientUnitRequest,
    ): ResponseEntity<ActionResponse> {
        val result = ingredientService.createIngredientUnit(
            name = request.name,
            userId = user.userId,
        )
        return ActionResponse.responseEntityForResult(result = result)
    }

    @DeleteMapping("/ingredient/unit/{ingredientUnitId}")
    fun deleteIngredientUnitById(
        @AuthenticationPrincipal user: UserAuthTokenVerifier.AuthenticatedUser,
        @PathVariable(value = "ingredientUnitId") ingredientUnitId: String,
    ): ResponseEntity<ActionResponse> {
        val result = ingredientService.deleteIngredientUnitById(
            ingredientUnitId = ingredientUnitId,
            userId = user.userId,
        )
        return ActionResponse.responseEntityForResult(result = result)
    }

    // ########################################################################
    // # Ingredient
    // ########################################################################
    @GetMapping("/ingredients")
    fun getAllIngredients() = ingredientService.getAllIngredients()

    @PostMapping("/ingredient", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createIngredient(
        @AuthenticationPrincipal user: UserAuthTokenVerifier.AuthenticatedUser,
        @RequestBody request: CreateIngredientRequest,
    ): ResponseEntity<ActionResponse> {
        val result = ingredientService.createIngredient(
            name = request.name,
            userId = user.userId,
        )
        return ActionResponse.responseEntityForResult(result = result)
    }

    @DeleteMapping("/ingredient/{ingredientId}")
    fun deleteIngredientById(
        @AuthenticationPrincipal user: UserAuthTokenVerifier.AuthenticatedUser,
        @PathVariable(value = "ingredientId") ingredientId: String,
    ): ResponseEntity<ActionResponse> {
        val result = ingredientService.deleteIngredientById(
            ingredientId = ingredientId,
            userId = user.userId,
        )
        return ActionResponse.responseEntityForResult(result = result)
    }

    // ########################################################################
    // # Ingredient Usage
    // ########################################################################
    @GetMapping("/ingredient/usages")
    fun getAllIngredientUsages(@RequestParam recipeId: String) =
        ingredientService.getAllIngredientUsagesForRecipe(recipeId)

    @PostMapping("/ingredient/usage", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createIngredientUsage(
        @AuthenticationPrincipal user: UserAuthTokenVerifier.AuthenticatedUser,
        @RequestBody request: CreateIngredientUsageRequest,
    ): ResponseEntity<ActionResponse> {
        val result = ingredientService.createIngredientUsage(
            recipeId = request.recipeId,
            ingredientId = request.ingredientId,
            ingredientUnitId = request.ingredientUnitId,
            amount = request.amount,
            userId = user.userId,
        )
        return ActionResponse.responseEntityForResult(result = result)
    }

    @PutMapping(
        "/ingredient/usage/{ingredientUsageId}",
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createIngredientUsage(
        @AuthenticationPrincipal user: UserAuthTokenVerifier.AuthenticatedUser,
        @PathVariable(value = "ingredientUsageId") ingredientUsageId: String,
        @RequestBody request: UpdateIngredientUsageRequest,
    ): ResponseEntity<ActionResponse> {
        val result = ingredientService.updateIngredientUsage(
            ingredientUsageId = ingredientUsageId,
            ingredientId = request.ingredientId,
            ingredientUnitId = request.ingredientUnitId,
            amount = request.amount,
            userId = user.userId,
        )
        return ActionResponse.responseEntityForResult(result = result)
    }

    @DeleteMapping("/ingredient/usage/{ingredientUsageId}")
    fun deleteIngredientUsageById(
        @AuthenticationPrincipal user: UserAuthTokenVerifier.AuthenticatedUser,
        @PathVariable(value = "ingredientUsageId") ingredientUsageId: String,
    ): ResponseEntity<ActionResponse> {
        val result =
            ingredientService.deleteIngredientUsageById(
                ingredientUsageId = ingredientUsageId,
                userId = user.userId,
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
