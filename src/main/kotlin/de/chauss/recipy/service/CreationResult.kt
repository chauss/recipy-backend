package de.chauss.recipy.service

enum class CreationResultStatus {
    CREATED,
    ALREADY_EXISTS,
    INVALID_ARGUMENTS
}

class CreationResult(
    val status: CreationResultStatus,
    val id: String? = null,
    val message: String = ""
)