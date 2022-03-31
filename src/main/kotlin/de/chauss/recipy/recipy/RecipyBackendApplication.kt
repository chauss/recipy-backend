package de.chauss.recipy.recipy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RecipyBackendApplication

fun main(args: Array<String>) {
	runApplication<RecipyBackendApplication>(*args)
}
