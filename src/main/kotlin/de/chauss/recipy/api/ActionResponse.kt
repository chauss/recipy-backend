package de.chauss.recipy.api

import de.chauss.recipy.service.ActionResult
import de.chauss.recipy.service.ActionResultStatus
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class ActionResponse(
    val id: String = "",
    val message: String = ""
) {
    companion object {
        fun responseEntityForResult(result: ActionResult): ResponseEntity<ActionResponse> {
            return when (result.status) {
                // 2xx
                ActionResultStatus.CREATED -> ResponseEntity(
                    ActionResponse(id = result.id!!),
                    HttpStatus.CREATED
                )
                ActionResultStatus.UPDATED -> ResponseEntity(
                    ActionResponse(id = result.id!!),
                    HttpStatus.OK
                )
                ActionResultStatus.DELETED -> ResponseEntity(
                    ActionResponse(id = result.id!!),
                    HttpStatus.OK
                )
                // 4xx
                ActionResultStatus.ELEMENT_NOT_FOUND -> ResponseEntity(
                    ActionResponse(message = result.message),
                    HttpStatus.NOT_FOUND
                )
                ActionResultStatus.ALREADY_EXISTS -> ResponseEntity(
                    ActionResponse(message = result.message),
                    HttpStatus.CONFLICT
                )
                ActionResultStatus.FAILED_TO_DELETE -> ResponseEntity(
                    ActionResponse(message = result.message),
                    HttpStatus.CONFLICT
                )
                ActionResultStatus.INVALID_ARGUMENTS -> ResponseEntity(
                    ActionResponse(message = result.message),
                    HttpStatus.CONFLICT
                )
            }
        }
    }
}