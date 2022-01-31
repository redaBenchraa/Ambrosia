package com.ambrosia.nymph.services

import com.ambrosia.nymph.models.KeycloakUser
import com.ambrosia.nymph.repositories.CustomerRepository
import com.ambrosia.nymph.repositories.EmployeeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("test")
class MockUserService(
    @Autowired private val employeeRepository: EmployeeRepository,
    @Autowired private val customerRepository: CustomerRepository,
) : AbstractUserService(employeeRepository, customerRepository) {

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
