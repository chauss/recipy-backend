package de.chauss.recipy.repositories.image

import kotlin.random.Random

interface ImageRepository {

    fun saveImage(bytes: ByteArray, recipeId: String, extension: String): String

    fun loadImage(imageId: String, recipeId: String): ByteArray?

    fun deleteImage(imageId: String, recipeId: String)

    fun deleteAllImagesForRecipe(recipeId: String)

    companion object {
        fun generateRandomImageId(extension: String): String {
            val imageRandomPartLength: Int = 10
            val imageNameCharPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
            val randomId = (1..imageRandomPartLength)
                .map {
                    Random.nextInt(0, imageNameCharPool.size)
                        .let { imageNameCharPool[it] }
                }
                .joinToString("")

            return "image_%s.%s".format(randomId, extension)
        }
    }
}