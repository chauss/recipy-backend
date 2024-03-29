package de.chauss.recipy.database.models

import jakarta.persistence.*
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
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
    @Fetch(FetchMode.SUBSELECT)
    val ingredientUsages: Set<IngredientUsage> = HashSet(),

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "recipe", cascade = [CascadeType.REMOVE])
    @Fetch(FetchMode.SUBSELECT)
    val preparationSteps: List<PreparationStep> = ArrayList(),

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "recipe", cascade = [CascadeType.REMOVE])
    @Fetch(FetchMode.SUBSELECT)
    val recipeImages: List<RecipeImage> = ArrayList(),

    val creator: String = "",

    val created: Instant = Instant.now(),
)

interface RecipeRepository : JpaRepository<Recipe, String> {
    fun findByName(name: String): List<Recipe>?
    fun findByCreator(creator: String): List<Recipe>?
}
