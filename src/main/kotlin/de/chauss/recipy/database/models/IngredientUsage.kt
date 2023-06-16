package de.chauss.recipy.database.models

import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.util.*

@Entity(name = "ingredient_usages")
class IngredientUsage(
    @Id
    val ingredientUsageId: String = "ingredient_usage_${UUID.randomUUID()}",
    @ManyToOne
    @JoinColumn(name = "ingredientId")
    var ingredient: Ingredient = Ingredient(),
    @ManyToOne
    @JoinColumn(name = "ingredientUnitId")
    var ingredientUnit: IngredientUnit = IngredientUnit(),
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recipeId")
    val recipe: Recipe = Recipe(),
    var amount: Double = 0.0,
    val created: Instant = Instant.now()
)

interface IngredientUsageRepository : JpaRepository<IngredientUsage, String> {
    fun findByRecipeRecipeId(recipeId: String): List<IngredientUsage>
    fun findByIngredientUnitIngredientUnitId(ingredientUnitId: String): List<IngredientUsage>
    fun findByIngredientIngredientId(ingredientId: String): List<IngredientUsage>
}
