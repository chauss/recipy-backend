package de.chauss.recipy.config

import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManagerResolver
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class WebSecurityConfig {

    @Bean
    fun filterChain(
        http: HttpSecurity,
        authenticationManagerResolver: AuthenticationManagerResolver<HttpServletRequest?>?
    ): SecurityFilterChain {
        http
            .oauth2ResourceServer { customizer ->
                customizer.authenticationManagerResolver(authenticationManagerResolver)
            }
            .authorizeHttpRequests { authz ->
                authz
                    .requestMatchers(
                        HttpMethod.GET,
                        // Recipe
                        "/api/v1/recipes",
                        "/api/v1/recipes/overview",
                        "/api/v1/recipes/overview/userId/{userId}",
                        "/api/v1/recipe/{recipeId}",
                        "/api/v1/recipe/{recipeId}/image/{imageId}",
                        "/api/v1/recipe/{recipeId}/images",
                        // Ingredients
                        "/api/v1/ingredient/units",
                        "/api/v1/ingredients",
                        "/api/v1/ingredient/usages",
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .csrf { csrfCustomizer -> csrfCustomizer.disable() }
            .httpBasic { httpCustomizer -> httpCustomizer.disable() }
            .sessionManagement { customizer ->
                customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }.cors { corsCustomizer ->
                corsCustomizer.configure(http) // Enable configuration via @CrossOrigin
            }

        return http.build()
    }

    @Bean
    fun authenticationManagerResolver(
        userAuthTokenVerifier: UserAuthTokenVerifier
    ): AuthenticationManagerResolver<HttpServletRequest> {
        return AuthenticationManagerResolver<HttpServletRequest> {
            ProviderManager(userAuthTokenVerifier)
        }
    }
}