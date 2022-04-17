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
                ActionResultStatus.CREATED -> {
                    ResponseEntity(ActionResponse(id = result.id!!), HttpStatus.CREATED)
                }
                ActionResultStatus.UPDATED -> {
                    ResponseEntity(ActionResponse(id = result.id!!), HttpStatus.OK)
                }
                else -> {
                    ResponseEntity(ActionResponse(message = result.message), HttpStatus.CONFLICT)
                }
            }
        }
    }
}