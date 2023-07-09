package de.chauss.recipy.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.io.File
import java.io.InputStream
import java.net.URL


@Configuration
class FirebaseConfig(
    @Value("\${google.application.credentials}") var googleCredentialsFile: String
) {
    private val logger = KotlinLogging.logger {}

    init {
        var inputStream: InputStream? = null
        // First check out the application.property path
        val credentialsFileFromAppProps = File(googleCredentialsFile)
        if (credentialsFileFromAppProps.exists() && credentialsFileFromAppProps.isFile) {
            inputStream = credentialsFileFromAppProps.inputStream()
        }

        // Second try the resources directory
        if (inputStream == null) {
            val credentialsFileFromResources: URL? =
                this::class.java.classLoader.getResource("google-application-credentials.json")
            credentialsFileFromResources?.let {
                inputStream = credentialsFileFromResources.openStream()
            }
        }

        val googleCredentials = inputStream?.let { GoogleCredentials.fromStream(inputStream) }

        val options = FirebaseOptions.builder()
            // Third fallback to applicationDefault
            .setCredentials(googleCredentials ?: GoogleCredentials.getApplicationDefault())
            .setProjectId("recipy-9b8ad")
            .build()

        // NOTE Especially necessary for ITs because they would like to reinitialize the firebase app
        if (FirebaseApp.getApps().isNotEmpty()) {
            FirebaseApp.getApps().forEach { it.delete() }
        }

        FirebaseApp.initializeApp(options)
        logger.info { "Initialized Firebase..." }
    }

}
