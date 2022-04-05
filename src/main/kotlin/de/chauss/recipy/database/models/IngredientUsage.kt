package de.chauss.recipy.database.models

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.util.UUID

@Entity(name = "ingredient_usages")
class IngredientUsage(
    @Id
    val ingredientUsageId: String = "ingredient_usage_${UUID.randomUUID()}",
    @OneToOne
    val ingredient: Ingredient = Ingredient(),
    @OneToOne
    val unit: IngredientUnit = IngredientUnit(),
    @ManyToOne
    val recipe: Recipe = Recipe(),
    val amount: Double = 0.0,
    val created: Instant = Instant.now()
)

interface IngredientUsageRepository : JpaRepository<IngredientUsage, String>