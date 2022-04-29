package de.chauss.recipy.database.models

import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

@Entity(name = "preparation_steps")
class PreparationStep (
    @Id
    val preparationStepId: String = "preparation_step_${UUID.randomUUID()}",
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipeId")
    val recipe: Recipe = Recipe(),
    val stepNumber: Int = 1,
    val description: String = "",
)

interface PreparationStepRepository : JpaRepository<PreparationStep, String>