package de.chauss.recipy.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.nimbusds.jose.util.StandardCharset
import de.chauss.recipy.crypto.FileEncrypterDecrypter
import mu.KotlinLogging
import org.springframework.beans.factory.BeanInitializationException
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.net.URL


@Configuration
class FirebaseConfig(
    @Value("\${recipy.encryption.encrypted-firebase-credentials-file}") val encryptedFirebaseCredentialsFile: String,
    @Value("\${recipy.encryption.firebase.secret-key}") val firebaseSecretKey: String,
) {
    private val logger = KotlinLogging.logger {}

    init {
        logger.info { "Going to initialize Firebase..." }
        val credentialsFileFromResources: URL =
            this::class.java.classLoader.getResource(encryptedFirebaseCredentialsFile)
                ?: throw BeanInitializationException("Could not find file $encryptedFirebaseCredentialsFile in resources which is necesary to initialize firebase.")

        logger.debug { "Going to decrypt firebase credentials-file..." }
        val firebaseCredentials =
            FileEncrypterDecrypter(firebaseSecretKey).decrypt(
                credentialsFileFromResources.openStream()
            )
                ?: throw BeanInitializationException("Could not decrypt file $encryptedFirebaseCredentialsFile with the given secret-key.")

        logger.debug { "Going to create GoogleCredentials with content from firebase credentials-file..." }
        val googleCredentials =
            GoogleCredentials.fromStream(firebaseCredentials.byteInputStream(StandardCharset.UTF_8))

        val options = FirebaseOptions.builder()
            .setCredentials(googleCredentials)
            .build()

        // NOTE Especially necessary for ITs because they would like to reinitialize the firebase app
        if (FirebaseApp.getApps().isNotEmpty()) {
            FirebaseApp.getApps().forEach { it.delete() }
        }

        FirebaseApp.initializeApp(options)
        logger.info { "Initialized Firebase..." }
    }

}
