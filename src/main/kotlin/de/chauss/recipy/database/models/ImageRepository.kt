package de.chauss.recipy.database.models

interface ImageRepository {

    fun saveImage(bytes: ByteArray, recipeId: String, extension: String): String

    fun loadImage(imageId: String, recipeId: String): ByteArray?

    fun deleteImage(imageId: String, recipeId: String)

    fun deleteAllImagesForRecipe(recipeId: String)

}