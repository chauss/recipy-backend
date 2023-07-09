package de.chauss.recipy

import com.ninjasquad.springmockk.MockkBean
import de.chauss.recipy.config.FirebaseConfig
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class RecipyBackendApplicationITests {

    @MockkBean
    lateinit var firebaseConfig: FirebaseConfig

    @Test
    fun contextLoads() {
    }
}
