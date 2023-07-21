package de.chauss.recipy.config

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebMvc
class WebMvcConfiguration(
    @Value("\${recipy.web-config.cors.allowed-origin}") val allowedOrigin: String,
) : WebMvcConfigurer {

    private val logger = KotlinLogging.logger {}

    init {
        logger.info { "Initializing WebMvcConfiguration." }
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        logger.info { "Setting cors allowed origins to $allowedOrigin" }
        registry.addMapping("/**").allowedOrigins(allowedOrigin)
        registry.addMapping("/**").allowedMethods("GET", "DELETE", "UPDATE", "POST")
    }
}
