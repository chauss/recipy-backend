package de.chauss.recipy.service

enum class ActionResultStatus {
    CREATED,
    FAILED_TO_CREATE,
    ALREADY_EXISTS,
    INVALID_ARGUMENTS,
    ELEMENT_NOT_FOUND,
    UPDATED,
    DELETED,
    FAILED_TO_DELETE,
    UNAUTHORIZED
}

class ActionResult(
    val status: ActionResultStatus,
    val id: String? = null,
    val message: String? = null,
    val errorCode: Int? = null
)