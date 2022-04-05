package de.chauss.recipy.database.models

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.util.UUID

@Entity(name = "recipes")
class Recipe(
    @Id
    val recipeId: String = "recipe_${UUID.randomUUID()}",
    val name: String = "",
    val created: Instant = Instant.now(),
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "recipe")
    val ingredientUsages: MutableSet<IngredientUsage> = HashSet()
) {
    fun addIngredientUsage(ingredientUsage: IngredientUsage) {
        ingredientUsages.add(ingredientUsage)
    }
}

interface RecipeRepository : JpaRepository<Recipe, String>{
    fun findByName(name: String?): List<Recipe>?
}
