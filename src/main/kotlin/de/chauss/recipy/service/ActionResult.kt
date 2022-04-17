package de.chauss.recipy.service

enum class ActionResultStatus {
    CREATED,
    ALREADY_EXISTS,
    INVALID_ARGUMENTS,
    ELEMENT_NOT_FOUND,
    UPDATED,
}

class ActionResult(
    val status: ActionResultStatus,
    val id: String? = null,
    val message: String = ""
)