package de.chauss.recipy.database.models

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

@Entity(name = "ingredients")
class Ingredient (
    @Id
    val ingredientId: String = "ingredient_${UUID.randomUUID()}",
    val name: String = ""
)

interface IngredientRepository : JpaRepository<Ingredient, String> {
    fun findByName(name: String?): List<Ingredient>?
}