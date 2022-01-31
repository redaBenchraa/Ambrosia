package com.ambrosia.nymph.services

import com.ambrosia.nymph.entities.Customer
import com.ambrosia.nymph.entities.Employee
import com.ambrosia.nymph.exceptions.EntityAlreadyExistsException
import com.ambrosia.nymph.models.KeycloakUser
import com.ambrosia.nymph.repositories.CustomerRepository
import com.ambrosia.nymph.repositories.EmployeeRepository
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractUserService(
    @Autowired private val employeeRepository: EmployeeRepository,
    @Autowired private val customerRepository: CustomerRepository,
) {

    fun verifyThatEmailDoesNotExists(email: String) {
        if (employeeRepository.existsByEmail(email)) {
            throw EntityAlreadyExistsException(Employee::class.java, mutableMapOf("email" to email))
        }
        if (customerRepository.existsByEmail(email)) {
            throw EntityAlreadyExistsException(Customer::class.java, mutableMapOf("email" to email))
        }
    }

    abstract fun createKeycloakUser(user: KeycloakUser)
    abstract fun updateEmail(user: KeycloakUser)
    abstract fun updateRoles(user: KeycloakUser)
    abstract fun deleteKeycloakUser(user: KeycloakUser)
}

