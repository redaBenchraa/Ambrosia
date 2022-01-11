package com.ambrosia.nymph.services

import com.ambrosia.nymph.configs.EnvironmentProperties
import com.ambrosia.nymph.models.KeycloakUser
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.AccessTokenResponse
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class KeycloakService {

	@Autowired
	lateinit var env: EnvironmentProperties

	fun getAccessToken(user: KeycloakUser): AccessTokenResponse {
		return getRealmUser(user).tokenManager()
			.accessToken
	}

	fun getUserRepresentation(user: KeycloakUser): UserRepresentation {
		val userRepresentation = UserRepresentation()
		userRepresentation.isEnabled = true
		userRepresentation.username = user.username
		userRepresentation.firstName = user.firstName
		userRepresentation.lastName = user.lastName
		userRepresentation.email = user.email
		userRepresentation.realmRoles = user.roles
		userRepresentation.credentials = listOf(getPasswordRepresentation(user.password))
		return userRepresentation
	}

	val realmManager: Keycloak
		get() = KeycloakBuilder.builder()
			.serverUrl(env.authServerUrl())
			.realm(env.realm())
			.grantType(OAuth2Constants.PASSWORD)
			.clientId(env.clientId())
			.clientSecret(env.clientSecret())
			.username(env.realmManagerUsername())
			.password(env.realmManagerPassword())
			.build()

	fun getRealmUser(user: KeycloakUser): Keycloak {
		return KeycloakBuilder.builder()
			.serverUrl(env.authServerUrl())
			.realm(env.realm())
			.grantType(OAuth2Constants.PASSWORD)
			.clientId(env.clientId())
			.clientSecret(env.clientSecret())
			.username(user.email)
			.password(user.password)
			.build()
	}

	fun getPasswordRepresentation(password: String?): CredentialRepresentation {
		val passwordCred = CredentialRepresentation()
		passwordCred.isTemporary = false
		passwordCred.type = CredentialRepresentation.PASSWORD
		passwordCred.value = password
		return passwordCred
	}
}