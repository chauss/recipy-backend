package de.chauss.recipy.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.context.annotation.Configuration


@Configuration
class FirebaseConfig {

    init {
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.getApplicationDefault())
            .setProjectId("recipy-9b8ad")
            .build()

        FirebaseApp.initializeApp(options)
    }
}