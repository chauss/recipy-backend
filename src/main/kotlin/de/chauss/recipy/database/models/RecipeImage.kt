package de.chauss.recipy.database.models

import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

@Entity(name = "recipe_images")
class RecipeImage(
    @Id
    val imageId: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipeId")
    val recipe: Recipe,

    var index: Int,

    val created: Instant = Instant.now(),
)

interface RecipeImageRepository : JpaRepository<RecipeImage, String>
