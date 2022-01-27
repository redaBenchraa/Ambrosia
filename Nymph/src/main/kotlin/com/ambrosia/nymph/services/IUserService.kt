package com.ambrosia.nymph.services

import com.ambrosia.nymph.models.KeycloakUser

interface IUserService {
    fun createKeycloakUser(user: KeycloakUser)
    fun updateEmail(user: KeycloakUser, email: String)
    fun updateRoles(user: KeycloakUser)
    fun deleteKeycloakUser(user: KeycloakUser)
}
