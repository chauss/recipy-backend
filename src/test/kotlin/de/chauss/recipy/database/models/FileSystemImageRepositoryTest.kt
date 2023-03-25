package de.chauss.recipy.database.models

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths

class FileSystemImageRepositoryTest {

    private val testAppDataDir = "/tmp"

    private val imageRepository: ImageRepository = FileSystemImageRepository(testAppDataDir)

    @Test
    fun shouldSaveImage() {
        // given
        val testImageBytes = Files.readAllBytes(Paths.get("src/test/resources/test_image.png"))
        val recipeId = "fake-recipe_save-image-test"

        // when
        val imageId = imageRepository.saveImage(testImageBytes, recipeId, "png")

        // then
        Assertions.assertThat(imageId).contains("image")
        Assertions.assertThat(imageId).contains("png")
        val savedImagePath =
            Paths.get("%s/recipy_images/%s/%s".format(testAppDataDir, recipeId, imageId))
        Assertions.assertThat(Files.exists(savedImagePath)).isTrue
        val savedImage = Files.readAllBytes(savedImagePath)
        Assertions.assertThat(testImageBytes).isEqualTo(savedImage)
    }

    @Test
    fun shouldBeAbleToLoadSavedImage() {
        // given
        val testImageBytes = Files.readAllBytes(Paths.get("src/test/resources/test_image.png"))
        val recipeId = "fake-recipe_load-image-test"
        val imageId = imageRepository.saveImage(testImageBytes, recipeId, "png")

        // when
        val savedImageBytes = imageRepository.loadImage(imageId, recipeId)

        // then
        Assertions.assertThat(testImageBytes).isEqualTo(savedImageBytes)
    }

    @Test
    fun shouldReturnNullWhenImageDoesNotExist() {
        // given
        val imageId = "image_that_does_not_exist"
        val recipeId = "recipe_that_does_not_exist"

        // when
        val savedImageBytes = imageRepository.loadImage(imageId, recipeId)

        // then
        Assertions.assertThat(savedImageBytes).isNull()
    }

    @Test
    fun shouldDeleteSavedImages() {
        // given
        val testImageBytes = Files.readAllBytes(Paths.get("src/test/resources/test_image.png"))
        val recipeId = "fake-recipe_save-image-test"
        val imageId = imageRepository.saveImage(testImageBytes, recipeId, "png")

        // when
        imageRepository.deleteImage(imageId, recipeId)

        // then
        val savedImagePath =
            Paths.get("%s/recipy_images/%s/%s".format(testAppDataDir, recipeId, imageId))
        Assertions.assertThat(Files.exists(savedImagePath)).isFalse
    }

    @Test
    fun shouldNotThrowWhenDeletingNonExistingImage() {
        // given
        val imageId = "image_that_does_not_exist"
        val recipeId = "recipe_that_does_not_exist"

        // when / then
        imageRepository.deleteImage(imageId, recipeId)
    }
}