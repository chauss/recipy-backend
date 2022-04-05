package de.chauss.recipy.database.models

import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.util.UUID

@Entity(name = "ingredient_usages")
class IngredientUsage(
    @Id
    val ingredientUsageId: String = "ingredient_usage_${UUID.randomUUID()}",
    @OneToOne
    @JoinColumn(name = "ingredientId")
    var ingredient: Ingredient = Ingredient(),
    @OneToOne
    @JoinColumn(name = "ingredientUnitId")
    var unit: IngredientUnit = IngredientUnit(),
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipeId")
    val recipe: Recipe = Recipe(),
    var amount: Double = 0.0,
    val created: Instant = Instant.now()
)

interface IngredientUsageRepository : JpaRepository<IngredientUsage, String> {
    fun findByRecipeRecipeId(recipeId: String): List<IngredientUsage>?
}
