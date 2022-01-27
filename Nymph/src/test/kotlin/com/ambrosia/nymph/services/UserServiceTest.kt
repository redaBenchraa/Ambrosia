package com.ambrosia.nymph.services

import com.ambrosia.nymph.constants.Role
import com.ambrosia.nymph.constants.Role.ADMIN
import com.ambrosia.nymph.constants.Role.EMPLOYEE
import com.ambrosia.nymph.constants.Role.MANAGER
import com.ambrosia.nymph.constants.Role.values
import com.ambrosia.nymph.exceptions.KeycloakException
import com.ambrosia.nymph.models.KeycloakUser
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.admin.client.resource.RoleMappingResource
import org.keycloak.admin.client.resource.RoleScopeResource
import org.keycloak.admin.client.resource.RolesResource
import org.keycloak.admin.client.resource.UserResource
import org.keycloak.admin.client.resource.UsersResource
import org.keycloak.representations.idm.RoleRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.boot.test.context.SpringBootTest
import java.net.URI
import java.util.*
import java.util.stream.Collectors
import javax.ws.rs.core.Response

@SpringBootTest
class UserServiceTest {

    private val keycloakService: KeycloakService = mockk(relaxed = true)
    private val keycloak: Keycloak = mockk(relaxed = true)
    private val realmResource: RealmResource = mockk(relaxed = true)
    private val usersResource: UsersResource = mockk(relaxed = true)
    private val rolesResource: RolesResource = mockk(relaxed = true)
    private val userResource: UserResource = mockk(relaxed = true)
    private val roleMappingResource: RoleMappingResource = mockk(relaxed = true)
    private val roleScopeResource: RoleScopeResource = mockk(relaxed = true)
    private val userService: UserService = UserService(keycloakService)

    @Test
    fun createUserUserResourceError() {
        every { keycloakService.realmManager() } returns keycloak
        every { keycloak.realm(any()) } returns realmResource
        every { realmResource.users() } returns null
        assertThrows<KeycloakException> { userService.createKeycloakUser(getKeycloakUser()) }
    }

    @Test
    fun createUserFailed() {
        every { keycloakService.getUsersResource() } returns usersResource
        every { usersResource.create(any()) } returns Response.notModified().build()
        assertThrows<KeycloakException> { userService.createKeycloakUser(getKeycloakUser()) }
    }

    @Test
    fun createUserSuccess() {
        every { keycloakService.getUsersResource() } returns usersResource
        every { usersResource.create(any()) } returns Response.created(URI.create("")).build()
        assertDoesNotThrow { userService.createKeycloakUser(getKeycloakUser()) }
    }

    @Test
    fun updateRolesUserNotFound() {
        every { keycloakService.getUsersResource() } returns usersResource
        every { usersResource.search(any()) } returns ArrayList()
        assertThrows<KeycloakException> { userService.createKeycloakUser(getKeycloakUser()) }
    }

    @Test
    fun updateRolesSuccess() {
        every { keycloakService.getUsersResource() } returns usersResource
        every { usersResource.search(any()) } returns listOf(getUserRepresentation(EMPLOYEE, MANAGER))
        every { realmResource.roles() } returns rolesResource
        every { rolesResource.list() } returns getRealmRoles()
        every { usersResource.get(any()) } returns userResource
        every { userResource.roles() } returns roleMappingResource
        every { roleMappingResource.realmLevel() } returns roleScopeResource
        assertDoesNotThrow { userService.updateRoles(getKeycloakUser(ADMIN)) }
    }

    @Test
    fun updateEmailSuccess() {
        every { keycloakService.getUsersResource() } returns usersResource
        every { usersResource.search(any()) } returns listOf(getUserRepresentation(EMPLOYEE, MANAGER))
        every { realmResource.roles() } returns rolesResource
        every { rolesResource.list() } returns getRealmRoles()
        every { usersResource.get(any()) } returns userResource
        every { userResource.roles() } returns roleMappingResource
        every { roleMappingResource.realmLevel() } returns roleScopeResource
        assertDoesNotThrow { userService.updateEmail(getKeycloakUser(ADMIN)) }
    }

    @Test
    fun deleteUser() {
        every { keycloakService.getUsersResource() } returns usersResource
        every { usersResource.search(any()) } returns listOf(getUserRepresentation(EMPLOYEE, MANAGER))
        every { realmResource.roles() } returns rolesResource
        every { rolesResource.list() } returns getRealmRoles()
        every { usersResource.get(any()) } returns userResource
        every { userResource.roles() } returns roleMappingResource
        every { roleMappingResource.realmLevel() } returns roleScopeResource
        assertDoesNotThrow { userService.deleteKeycloakUser(getKeycloakUser(ADMIN)) }
    }

    fun getRoleRepresentation(role: String?): RoleRepresentation {
        val roleRepresentation = RoleRepresentation()
        roleRepresentation.name = role
        return roleRepresentation
    }

    fun getRealmRoles(): MutableList<RoleRepresentation>? = Arrays.stream(values())
        .map { x -> getRoleRepresentation(x.name) }
        .collect(Collectors.toList())

    private fun getUserRepresentation(vararg roles: Role): UserRepresentation {
        val userRepresentation = UserRepresentation()
        userRepresentation.id = "id"
        userRepresentation.username = "username"
        userRepresentation.firstName = "firstName"
        userRepresentation.lastName = "lastName"
        userRepresentation.email = "email"
        userRepresentation.realmRoles = Arrays.stream(roles)
            .map { obj: Enum<*> -> obj.name }
            .collect(Collectors.toList())
        return userRepresentation
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
