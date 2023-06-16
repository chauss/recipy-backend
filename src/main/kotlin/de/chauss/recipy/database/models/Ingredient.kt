package de.chauss.recipy.database.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.util.*

@Entity(name = "ingredients")
class Ingredient(
    @Id
    val ingredientId: String = "ingredient_${UUID.randomUUID()}",
    @Column(unique = true)
    val name: String = "",
    val creator: String = "",
    val created: Instant = Instant.now(),
)

interface IngredientRepository : JpaRepository<Ingredient, String> {
    fun findByNameIgnoreCase(name: String): Ingredient?
}
