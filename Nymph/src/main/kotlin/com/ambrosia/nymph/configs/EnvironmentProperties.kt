package com.ambrosia.nymph.configs

import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class EnvironmentProperties(private val env: Environment) {
    fun realm(): String? {
        return env.getProperty("keycloak.realm")
    }

    fun clientId(): String? {
        return env.getProperty("keycloak.resource")
    }

    fun authServerUrl(): String? {
        return env.getProperty("keycloak.auth-server-url")
    }

    fun clientSecret(): String? {
        return env.getProperty("keycloak.credentials.secret")
    }

    fun realmManagerUsername(): String? {
        return env.getProperty("realm-manager-username")
    }

    fun realmManagerPassword(): String? {
        return env.getProperty("realm-manager-password")
    }
}
