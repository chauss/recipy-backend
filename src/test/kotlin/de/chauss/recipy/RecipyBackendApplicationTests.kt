package de.chauss.recipy

import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootTest
@Profile("local")
class RecipyBackendApplicationTests {

	@Test
	fun contextLoads() {
	}

}
