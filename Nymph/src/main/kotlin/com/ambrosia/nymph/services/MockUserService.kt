package com.ambrosia.nymph.services

import com.ambrosia.nymph.models.KeycloakUser
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("test")
class MockUserService : IUserService {
    override fun verifyThatEmailDoesNotExists(email: String) {
        return
    }

    override fun createKeycloakUser(user: KeycloakUser) {
        return
    }

    override fun updateEmail(user: KeycloakUser) {
        return
    }

    override fun updateRoles(user: KeycloakUser) {
        return
    }

    override fun deleteKeycloakUser(user: KeycloakUser) {
        return
    }
}
