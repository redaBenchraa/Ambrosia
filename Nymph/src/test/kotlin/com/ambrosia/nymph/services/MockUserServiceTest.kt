package com.ambrosia.nymph.services

import com.ambrosia.nymph.constants.Role
import com.ambrosia.nymph.models.KeycloakUser
import com.ambrosia.nymph.repositories.CustomerRepository
import com.ambrosia.nymph.repositories.EmployeeRepository
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.util.Arrays
import java.util.stream.Collectors

class MockUserServiceTest {

    private val employeeRepository: EmployeeRepository = mockk(relaxed = true)
    private val customerRepository: CustomerRepository = mockk(relaxed = true)
    private val userService: MockUserService = MockUserService(employeeRepository, customerRepository)

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
            roles = Arrays.stream(roles)
                .map { obj: Enum<*> -> obj.name }
                .collect(Collectors.toList()))
    }
}
