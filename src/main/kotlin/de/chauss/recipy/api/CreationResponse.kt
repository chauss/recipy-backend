package de.chauss.recipy.api

import de.chauss.recipy.service.CreationResult
import de.chauss.recipy.service.CreationResultStatus
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class CreationResponse(
    val id: String = "",
    val message: String = ""
) {
    companion object {
        fun responseEntityForResult(result: CreationResult): ResponseEntity<CreationResponse> {
            return if (result.status == CreationResultStatus.CREATED) {
                ResponseEntity(CreationResponse(id = result.id!!), HttpStatus.CREATED)
            } else {
                ResponseEntity(CreationResponse(message = result.message), HttpStatus.CONFLICT)
            }
        }
    }
}