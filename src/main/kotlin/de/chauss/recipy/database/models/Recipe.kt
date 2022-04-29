package de.chauss.recipy.database.models

import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.util.*

@Entity(name = "recipes")
class Recipe(
    @Id
    val recipeId: String = "recipe_${UUID.randomUUID()}",
    @Column(unique = true)
    val name: String = "",
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "recipe", cascade = [CascadeType.REMOVE])
    val ingredientUsages: Set<IngredientUsage> = HashSet(),
    val created: Instant = Instant.now(),
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "recipe", cascade = [CascadeType.REMOVE])
    val preparationSteps: List<PreparationStep> = ArrayList(),
)

interface RecipeRepository : JpaRepository<Recipe, String> {
    fun findByName(name: String): List<Recipe>?
}
