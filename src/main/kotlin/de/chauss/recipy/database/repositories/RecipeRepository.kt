package de.chauss.recipy.database.repositories

import de.chauss.recipy.database.models.Recipe
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID

interface RecipeRepository : JpaRepository<Recipe, UUID>