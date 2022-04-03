package de.chauss.recipy.database


enum class CreationResultStatus {
    CREATED,
    ALREADY_EXISTS
}

class CreationResult(
    val status: CreationResultStatus,
    val id: String? = null
)