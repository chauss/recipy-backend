package de.chauss.recipy.database.models

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

@Entity
class Recipe(
    @Id
    val recipeId: String = "recipe_${UUID.randomUUID()}",
    val name: String = ""
)

interface RecipeRepository : JpaRepository<Recipe, String>