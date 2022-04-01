package de.chauss.recipy.database.models

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "recipes")
class Recipe(
    @Id
    @Column
    var id: UUID,

    @Column(nullable = false)
    var name: String
)
