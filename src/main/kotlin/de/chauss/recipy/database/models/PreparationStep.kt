package de.chauss.recipy.database.models

import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.util.*

@Entity(name = "preparation_steps")
class PreparationStep(
    @Id
    val preparationStepId: String = "preparation_step_${UUID.randomUUID()}",
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recipeId")
    val recipe: Recipe = Recipe(),
    var stepNumber: Int = 1,
    var description: String = "",
    val created: Instant = Instant.now(),
)

interface PreparationStepRepository : JpaRepository<PreparationStep, String>