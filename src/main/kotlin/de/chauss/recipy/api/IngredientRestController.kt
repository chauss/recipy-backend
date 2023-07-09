package de.chauss.recipy.api;

import de.chauss.recipy.config.UserAuthTokenVerifier
import de.chauss.recipy.service.IngredientService
import de.chauss.recipy.service.dtos.IngredientDto
import de.chauss.recipy.service.dtos.IngredientUnitDto
import de.chauss.recipy.service.dtos.IngredientUsageDto
import mu.KotlinLogging
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
    private val logger = KotlinLogging.logger {}

    // ########################################################################
    // # Ingredient Unit
    // ########################################################################
    @GetMapping("/ingredient/units")
    fun getAllIngredientUnits(): List<IngredientUnitDto> {
        logger.debug { "Requesting all ingredientUnits..." }
        return ingredientService.getAllIngredientUnits()
    }

    @PostMapping("/ingredient/unit", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createIngredientUnit(
        @AuthenticationPrincipal user: UserAuthTokenVerifier.AuthenticatedUser,
        @RequestBody request: CreateIngredientUnitRequest,
    ): ResponseEntity<ActionResponse> {
        logger.debug { "Creating ingredientUnit with name=${request.name}. AuthenticatedUserId=${user.userId}..." }
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
        logger.debug { "Deleting ingredientUnit with id=$ingredientUnitId. AuthenticatedUserId=${user.userId}..." }
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
    fun getAllIngredients(): List<IngredientDto> {
        logger.debug { "Requesting all ingredients..." }
        return ingredientService.getAllIngredients()
    }

    @PostMapping("/ingredient", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createIngredient(
        @AuthenticationPrincipal user: UserAuthTokenVerifier.AuthenticatedUser,
        @RequestBody request: CreateIngredientRequest,
    ): ResponseEntity<ActionResponse> {
        logger.debug { "Creating ingredient with name=${request.name}. AuthenticatedUserId=${user.userId}..." }
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
        logger.debug { "Deleting ingredient with id=$ingredientId. AuthenticatedUserId=${user.userId}..." }
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
    fun getAllIngredientUsages(@RequestParam recipeId: String): List<IngredientUsageDto> {
        logger.debug { "Requesting all ingredientUsages for recipe with id=$recipeId..." }
        return ingredientService.getAllIngredientUsagesForRecipe(recipeId)
    }

    @PostMapping("/ingredient/usage", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createIngredientUsage(
        @AuthenticationPrincipal user: UserAuthTokenVerifier.AuthenticatedUser,
        @RequestBody request: CreateIngredientUsageRequest,
    ): ResponseEntity<ActionResponse> {
        logger.debug { "Creating ingredientUsage for recupe with id=${request.recipeId}. AuthenticatedUserId=${user.userId}..." }
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
    fun updateIngredientUsage(
        @AuthenticationPrincipal user: UserAuthTokenVerifier.AuthenticatedUser,
        @PathVariable(value = "ingredientUsageId") ingredientUsageId: String,
        @RequestBody request: UpdateIngredientUsageRequest,
    ): ResponseEntity<ActionResponse> {
        logger.debug { "Updating ingredientUsage with id=$ingredientUsageId. AuthenticatedUserId=${user.userId}..." }
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
        logger.debug { "Deleting ingredientUsage with id=$ingredientUsageId. AuthenticatedUserId=${user.userId}..." }
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
