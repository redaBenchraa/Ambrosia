package com.ambrosia.nymph.services

import com.ambrosia.nymph.constants.Role
import com.ambrosia.nymph.models.KeycloakUser
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.boot.test.context.SpringBootTest
import java.util.Arrays
import java.util.stream.Collectors

@SpringBootTest
class MockUserServiceTest {

    private val userService: MockUserService = MockUserService()

    @Test
    fun verifyThatEmailDoesNotExists() {
        assertDoesNotThrow { userService.verifyThatEmailDoesNotExists("") }
    }

    @Test
    fun createKeycloakUser() {
        assertDoesNotThrow { userService.createKeycloakUser(getKeycloakUser()) }
    }

    @Test
    fun updateEmail() {
        assertDoesNotThrow { userService.updateEmail(getKeycloakUser()) }
    }

    @Test
    fun updateRoles() {
        assertDoesNotThrow { userService.updateRoles(getKeycloakUser()) }
    }

    @Test
    fun deleteKeycloakUser() {
        assertDoesNotThrow { userService.deleteKeycloakUser(getKeycloakUser()) }
    }

    private fun getKeycloakUser(vararg roles: Role): KeycloakUser {
        return KeycloakUser(
            id = "id",
            username = "username",
            password = "password",
            firstName = "firstName",
            lastName = "lastName",
            email = "email",
            roles =
            Arrays.stream(roles)
                .map { obj: Enum<*> -> obj.name }
                .collect(Collectors.toList()))
    }
}
