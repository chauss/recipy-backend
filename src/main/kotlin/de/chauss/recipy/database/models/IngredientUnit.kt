package de.chauss.recipy.database.models

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.util.*

@Entity(name = "ingredient_units")
class IngredientUnit (
    @Id
    val ingredientUnitId: String = "ingredient_unit_${UUID.randomUUID()}",
    val name: String = "",
    val created: Instant = Instant.now()
)

interface IngredientUnitRepository : JpaRepository<IngredientUnit, String> {

    fun findByName(name: String?): List<IngredientUnit>?
}
