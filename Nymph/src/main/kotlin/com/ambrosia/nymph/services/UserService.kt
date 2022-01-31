package com.ambrosia.nymph.services

import com.ambrosia.nymph.entities.Customer
import com.ambrosia.nymph.entities.Employee
import com.ambrosia.nymph.exceptions.EntityAlreadyExistsException
import com.ambrosia.nymph.exceptions.KeycloakException
import com.ambrosia.nymph.models.KeycloakUser
import com.ambrosia.nymph.repositories.CustomerRepository
import com.ambrosia.nymph.repositories.EmployeeRepository
import org.apache.commons.collections4.CollectionUtils.isNotEmpty
import org.apache.commons.collections4.CollectionUtils.subtract
import org.apache.http.HttpStatus
import org.keycloak.representations.idm.RoleRepresentation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.util.Objects.nonNull
import java.util.stream.Collectors

@Service
@Profile("default")
class UserService(
    @Autowired private val keycloakService: KeycloakService,
    @Autowired private val employeeRepository: EmployeeRepository,
    @Autowired private val customerRepository: CustomerRepository,
) : IUserService {

    override fun verifyThatEmailDoesNotExists(email: String) {
        if (employeeRepository.existsByEmail(email)) {
            throw EntityAlreadyExistsException(Employee::class.java, mutableMapOf("email" to email))
        }
        if (customerRepository.existsByEmail(email)) {
            throw EntityAlreadyExistsException(Customer::class.java, mutableMapOf("email" to email))
        }
    }

    override fun createKeycloakUser(user: KeycloakUser) {
        val usersResource = keycloakService.getUsersResource()
        val userRepresentation = keycloakService.getUserRepresentation(user)
        usersResource.create(userRepresentation).use { response ->
            if (response.status != HttpStatus.SC_CREATED) {
                throw KeycloakException("error.keycloak.createUser")
            }
        }
    }

    override fun updateEmail(user: KeycloakUser) {
        val usersResource = keycloakService.getUsersResource()
        val currentUser =
            usersResource.search(user.username).stream().findFirst()
                .orElseThrow { KeycloakException("error.keycloak.userNotFound") }
        usersResource[currentUser.id].update(currentUser)
    }

    override fun updateRoles(user: KeycloakUser) {
        val usersResource = keycloakService.getUsersResource()
        val currentUser =
            usersResource.search(user.username).stream().findFirst()
                .orElseThrow { KeycloakException("error.keycloak.userNotFound") }
        val realmRoles = keycloakService.getReamResource().roles().list()
        val currentRoles =
            usersResource[currentUser.id]
                .roles()
                .all
                .realmMappings
                .stream()
                .map { obj: RoleRepresentation -> obj.name }
                .collect(Collectors.toList())
        val rolesToAdd: MutableList<RoleRepresentation>? =
            subtract(user.roles, currentRoles)
                .stream()
                .map { role ->
                    realmRoles
                        .stream()
                        .filter { x: RoleRepresentation -> (x.name == role) }
                        .findFirst()
                        .orElse(null)
                }
                .collect(Collectors.toList())
        val rolesToRemove: MutableList<RoleRepresentation>? =
            subtract(currentRoles, user.roles)
                .stream()
                .map { role ->
                    realmRoles
                        .stream()
                        .filter { x: RoleRepresentation -> (x.name == role) }
                        .findFirst()
                        .orElse(null)
                }
                .collect(Collectors.toList())
        if (isNotEmpty(rolesToRemove)) {
            usersResource[currentUser.id].roles().realmLevel().remove(rolesToRemove)
        }
        if (isNotEmpty(rolesToAdd)) {
            usersResource[currentUser.id].roles().realmLevel().add(rolesToAdd)
        }
    }

    override fun deleteKeycloakUser(user: KeycloakUser) {
        val usersResource = keycloakService.getUsersResource()
        val currentUser = usersResource.search(user.username).stream().findFirst().orElse(null)
        if (nonNull(currentUser)) {
            usersResource.delete(currentUser.id)
        }
    }
}
