package com.ambrosia.nymph.services

import com.ambrosia.nymph.models.KeycloakUser

abstract class AbstractUserService {
    abstract fun createKeycloakUser(user: KeycloakUser)
    abstract fun updateEmail(user: KeycloakUser, email: String)
    abstract fun updateRoles(user: KeycloakUser)
    abstract fun deleteKeycloakUser(user: KeycloakUser)
}
