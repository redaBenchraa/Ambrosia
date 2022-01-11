package com.ambrosia.nymph.configs

import org.keycloak.adapters.KeycloakConfigResolver
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KeycloakResolver {
	@Bean
	fun keycloakConfigResolver(): KeycloakConfigResolver {
		return KeycloakSpringBootConfigResolver()
	}
}