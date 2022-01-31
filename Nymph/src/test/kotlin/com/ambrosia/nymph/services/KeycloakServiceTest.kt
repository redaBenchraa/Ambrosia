package com.ambrosia.nymph.services

import com.ambrosia.nymph.configs.EnvironmentProperties
import com.ambrosia.nymph.models.KeycloakUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.admin.client.resource.UsersResource

internal class KeycloakServiceTest {

    private val environmentProperties: EnvironmentProperties = mockk(relaxed = true)
    private val keycloakService = spyk(KeycloakService(environmentProperties))

    @Test
    fun getUserRepresentation() {
        val user = KeycloakUser(
            id = "id",
            username = "username",
            password = "password",
            email = "email",
            firstName = "firstName",
            lastName = "lastName",
            roles = mutableListOf("MANAGER"))
        val result = keycloakService.getUserRepresentation(user)
        assertEquals(result.username, "username")
        assertEquals(result.email, "email")
        assertEquals(result.credentials[0].type, "password")
        assertEquals(result.credentials[0].isTemporary, false)
        assertEquals(result.credentials[0].value, "password")
        assertTrue(result.isEnabled)
    }

    @Test
    fun getPasswordRepresentation() {
        val result = keycloakService.getPasswordRepresentation("password")
        assertEquals(result.type, "password")
        assertEquals(result.isTemporary, false)
        assertEquals(result.value, "password")
    }

    @Test
    fun realmManager() {
        val result = keycloakService.realmManager()
        assertNotNull(result)
        assertNotNull(result.tokenManager())
        assertFalse(result.isClosed)
    }

    @Test
    fun getReamResource() {
        val keycloak: Keycloak = mockk(relaxed = true)
        val realm: RealmResource = mockk(relaxed = true)
        every { keycloakService.realmManager() } returns keycloak
        every { keycloak.realm(any()) } returns realm
        val result = keycloakService.getReamResource()
        assertNotNull(result)
    }

    @Test
    fun getRealmUser() {
        val user = KeycloakUser(
            id = "id",
            username = "username",
            password = "password",
            email = "email",
            firstName = "firstName",
            lastName = "lastName",
            roles = mutableListOf("MANAGER"))
        val result = keycloakService.getRealmUser(user)
        assertNotNull(result)
        assertNotNull(result.tokenManager())
        assertFalse(result.isClosed)
    }

    @Test
    fun getUsersResource() {
        val realm: RealmResource = mockk(relaxed = true)
        val users: UsersResource = mockk(relaxed = true)
        every { keycloakService.getReamResource() } returns realm
        every { realm.users() } returns users
        val result = keycloakService.getUsersResource()
        assertNotNull(result)
    }
}
