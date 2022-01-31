package com.ambrosia.nymph.services

import com.ambrosia.nymph.models.KeycloakUser

interface IUserService {
    fun verifyThatEmailDoesNotExists(email: String)
    fun createKeycloakUser(user: KeycloakUser)
    fun updateEmail(user: KeycloakUser)
    fun updateRoles(user: KeycloakUser)
    fun deleteKeycloakUser(user: KeycloakUser)
}

