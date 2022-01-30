package com.ambrosia.nymph.services

import com.ambrosia.nymph.configs.EnvironmentProperties
import com.ambrosia.nymph.models.KeycloakUser
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.admin.client.resource.UsersResource
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class KeycloakService(@Autowired private val environmentProperties: EnvironmentProperties) {

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

    final fun getReamResource(): RealmResource = realmManager().realm(environmentProperties.realm())

    final fun realmManager(): Keycloak = KeycloakBuilder.builder()
        .serverUrl(environmentProperties.authServerUrl())
        .realm(environmentProperties.realm())
        .grantType(OAuth2Constants.PASSWORD)
        .clientId(environmentProperties.clientId())
        .clientSecret(environmentProperties.clientSecret())
        .username(environmentProperties.realmManagerUsername())
        .password(environmentProperties.realmManagerPassword())
        .build()

    fun getRealmUser(user: KeycloakUser): Keycloak = KeycloakBuilder.builder()
        .serverUrl(environmentProperties.authServerUrl())
        .realm(environmentProperties.realm())
        .grantType(OAuth2Constants.PASSWORD)
        .clientId(environmentProperties.clientId())
        .clientSecret(environmentProperties.clientSecret())
        .username(user.email)
        .password(user.password)
        .build()

    fun getPasswordRepresentation(password: String): CredentialRepresentation =
        CredentialRepresentation().apply {
            isTemporary = false
            type = CredentialRepresentation.PASSWORD
            value = password
        }

    fun getUsersResource(): UsersResource {
        return getReamResource().users()
    }
}
