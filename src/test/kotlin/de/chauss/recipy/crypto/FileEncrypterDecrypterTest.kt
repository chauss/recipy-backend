package de.chauss.recipy.crypto

import com.nimbusds.jose.util.StandardCharset
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileNotFoundException
import java.net.URL
import java.util.*
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class FileEncrypterDecrypterTest {

    @Test
    fun `encrypted content is the same after decryption`() {
        // Given
        val originalContent = "my dirty little secret"
        val fileName = "encryptiontest.enc"
        val secretKey: SecretKey = KeyGenerator.getInstance("AES").generateKey()
        val fileEncrypterDecrypter = FileEncrypterDecrypter(secretKey)

        // When
        fileEncrypterDecrypter.encrypt(originalContent, fileName)
        val decryptedContent: String? = fileEncrypterDecrypter.decryptFile(fileName)

        // Then
        Assertions.assertEquals(decryptedContent, originalContent)
        File(fileName).delete() // cleanup
    }

    @Test
    @Disabled
    fun `create encrypted file`() {
        val fileName = "google-application-credentials.json"
        val fileUrl: URL = this::class.java.classLoader.getResource(fileName)
            ?: throw FileNotFoundException("Could not find a file to encrypt with the name $fileName in resources!")
        val contentToEncrypt = fileUrl.readText(StandardCharset.UTF_8)

        println("The content is: $contentToEncrypt")
        val secretKey: SecretKey = KeyGenerator.getInstance("AES").generateKey()

        val fileEncrypterDecrypter = FileEncrypterDecrypter(secretKey)

        fileEncrypterDecrypter.encrypt(contentToEncrypt, "$fileName.enc")
        println("The secret key is: ${Base64.getEncoder().encodeToString(secretKey.encoded)}")
    }

    @Test
    @Disabled
    fun `read encrypted file`() {
        val fileName = "google-application-credentials.json.enc"
        val secretKeyAsString = "BiMEot/fWOAPWSsYrZT81g=="
        val fileEncrypterDecrypter =
            FileEncrypterDecrypter(secretKeyAsString)

        val decryptedFileContent = fileEncrypterDecrypter.decryptFile(fileName)
        println("The secret is: $decryptedFileContent")
    }

    @Test
    @Disabled
    fun `read encrypted inputStream`() {
        val fileName = "google-application-credentials.json.enc"
        val secretKeyAsString = "BiMEot/fWOAPWSsYrZT81g=="

        val fileUrl: URL = this::class.java.classLoader.getResource(fileName)
            ?: throw FileNotFoundException("Could not find a file to decrypt with the name $fileName in resources!")
        val inputStream = fileUrl.openStream()
        val fileEncrypterDecrypter =
            FileEncrypterDecrypter(secretKeyAsString)

        val decryptedFileContent = fileEncrypterDecrypter.decrypt(inputStream)
        println("The secret is: $decryptedFileContent")
    }
}