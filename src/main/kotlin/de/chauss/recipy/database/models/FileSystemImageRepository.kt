package de.chauss.recipy.database.models

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.random.Random

@Component
class FileSystemImageRepository(
    @Value("\${recipy.data.images.path}") var appDataDir: String
) : ImageRepository {

    private val imageRandomPartLength: Int = 10

    val imageDirName: String = "recipy_images"

    private val imageNameCharPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    override fun saveImage(bytes: ByteArray, recipeId: String, extension: String): String {
        val imageDirForRecipe = imageDirForRecipe(recipeId)
        Files.createDirectories(Paths.get(imageDirForRecipe))

        val imageId = generateRandomImageId(extension)
        val imagePath = "%s/%s".format(imageDirForRecipe, imageId)

        Files.write(Paths.get(imagePath), bytes)

        return imageId
    }

    override fun loadImage(imageId: String, recipeId: String): ByteArray? {
        val imageDirForRecipe = imageDirForRecipe(recipeId)
        val imagePath = Paths.get("%s/%s".format(imageDirForRecipe, imageId))

        if (Files.exists(imagePath)) {
            return Files.readAllBytes(imagePath)
        }

        return null
    }

    override fun deleteImage(imageId: String, recipeId: String) {
        val imageDirForRecipe = imageDirForRecipe(recipeId)
        val imagePath = Paths.get("%s/%s".format(imageDirForRecipe, imageId))
        Files.deleteIfExists(imagePath)
    }

    override fun deleteAllImagesForRecipe(recipeId: String) {
        val imageDirForRecipe = imageDirForRecipe(recipeId)
        Files.deleteIfExists(Paths.get(imageDirForRecipe))
    }

    private fun imageDirForRecipe(recipeId: String): String {
        return "%s/%s/%s".format(appDataDir, imageDirName, recipeId)
    }

    private fun generateRandomImageId(extension: String): String {
        val randomId = (1..imageRandomPartLength)
            .map {
                Random.nextInt(0, imageNameCharPool.size)
                    .let { imageNameCharPool[it] }
            }
            .joinToString("")

        return "image_%s.%s".format(randomId, extension)
    }

}