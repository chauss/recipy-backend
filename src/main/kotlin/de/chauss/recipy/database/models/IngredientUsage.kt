package de.chauss.recipy.database.models

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

@Entity(name = "ingredient_usages")
class IngredientUsage(
    @Id
    val ingredientId: String = "ingredient_usage_${UUID.randomUUID()}",
    @OneToOne
    val ingredient: Ingredient = Ingredient(),
    val amount: Int = 0,
)

interface IngredientUsageRepository : JpaRepository<IngredientUsage, String>