package com.ambrosia.nymph.configs

import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class EnvironmentProperties(private val env: Environment) {

    fun realm(): String? = env.getProperty("keycloak.realm")

    fun clientId(): String? = env.getProperty("keycloak.resource")

    fun authServerUrl(): String? = env.getProperty("keycloak.auth-server-url")

    fun clientSecret(): String? = env.getProperty("keycloak.credentials.secret")

    fun realmManagerUsername(): String? = env.getProperty("realm-manager-username")

    fun realmManagerPassword(): String? = env.getProperty("realm-manager-password")
}
