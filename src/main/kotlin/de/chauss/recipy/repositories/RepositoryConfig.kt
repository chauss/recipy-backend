package de.chauss.recipy.repositories

import de.chauss.recipy.repositories.image.FileSystemImageRepository
import de.chauss.recipy.repositories.image.GcsBucketImageRepository
import de.chauss.recipy.repositories.image.ImageRepository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RepositoryConfig {
    private val logger = KotlinLogging.logger {}

    @Bean
    @ConditionalOnProperty(name = ["recipy.repositories.image"], havingValue = "file_system")
    fun fileSystemImageRepository(
        @Value("\${recipy.data.images.path}") appDataDir: String
    ): ImageRepository {
        logger.info { "ImageRepository: Using FileSystemImageRepository" }
        return FileSystemImageRepository(appDataDir)
    }

    @Bean
    @ConditionalOnProperty(name = ["recipy.repositories.image"], havingValue = "gcs_bucket")
    fun gcpBucketImageRepository(
        @Value("\${recipy.repositories.image.gcs_bucket.name}") gcsBucketName: String
    ): ImageRepository {
        logger.info { "ImageRepository: Using GcsBucketImageRepository" }
        return GcsBucketImageRepository(gcsBucketName)
    }
}