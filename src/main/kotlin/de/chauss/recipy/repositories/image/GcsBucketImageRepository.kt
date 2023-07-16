package de.chauss.recipy.repositories.image

import com.google.cloud.storage.Bucket
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions


class GcsBucketImageRepository(
    gcsBucketName: String
) : ImageRepository {
    private val storage: Storage = StorageOptions.getDefaultInstance().service
    private val bucket: Bucket = storage.get(gcsBucketName)

    override fun saveImage(bytes: ByteArray, recipeId: String, extension: String): String {
        val imageId = ImageRepository.generateRandomImageId(extension)
        bucket.create("$recipeId/$imageId", bytes)
        return imageId
    }

    override fun loadImage(imageId: String, recipeId: String): ByteArray? {
        val blob = bucket.get("$recipeId/$imageId")
        if (blob.exists()) {
            return blob.getContent()
        }
        return null
    }

    override fun deleteImage(imageId: String, recipeId: String) {
        val blob = bucket.get("$recipeId/$imageId")
        if (blob.exists()) {
            blob.delete()
        }
    }

    override fun deleteAllImagesForRecipe(recipeId: String) {
        val batch = storage.batch()
        val blobs = bucket.list(
            Storage.BlobListOption.prefix(recipeId)
        )
        for (blob in blobs.iterateAll()) {
            batch.delete(blob.blobId)
        }
        batch.submit()
    }
}