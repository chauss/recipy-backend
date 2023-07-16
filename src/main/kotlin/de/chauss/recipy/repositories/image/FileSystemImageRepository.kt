package de.chauss.recipy.repositories.image

import de.chauss.recipy.repositories.image.ImageRepository.Companion.generateRandomImageId
import org.springframework.beans.factory.annotation.Value
import java.nio.file.Files
import java.nio.file.Paths

class FileSystemImageRepository(
    @Value("\${recipy.data.images.path}") var appDataDir: String
) : ImageRepository {

    val imageDirName: String = "recipy_images"

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


}