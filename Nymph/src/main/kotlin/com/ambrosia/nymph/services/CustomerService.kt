package com.ambrosia.nymph.services

import com.ambrosia.nymph.dtos.CustomerDto
import com.ambrosia.nymph.dtos.CustomerRegistrationDto
import com.ambrosia.nymph.dtos.EditEmailDto
import com.ambrosia.nymph.entities.Customer
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.mappers.toEntity
import com.ambrosia.nymph.mappers.toKeyCloakUser
import com.ambrosia.nymph.repositories.CustomerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class CustomerService(
    @Autowired private val customerRepository: CustomerRepository,
    @Autowired private val userService: AbstractUserService,
) {
    @Transactional
    fun addCustomer(customerDto: CustomerRegistrationDto): CustomerDto {
        customerDto.email?.let { userService.verifyThatEmailDoesNotExists(it) }
        userService.createKeycloakUser(customerDto.toKeyCloakUser())
        val customer = customerDto.toEntity()
        return customerRepository.save(customer).toDto()
    }

    @Transactional
    fun editCustomerEmail(customerId: Long, editEmailDto: EditEmailDto): CustomerDto {
        val customer = customerRepository.findById(customerId)
            .orElseThrow { EntityNotFoundException(Customer::class.java, mutableMapOf("id" to customerId)) }
        editEmailDto.email?.let {
            userService.updateEmail(customer.toDto().toKeyCloakUser().copy(email = it))
            customer.email = it
        }
        customerRepository.save(customer)
        return customer.toDto()
    }

    @Transactional
    fun editCustomer(customerId: Long, customerDto: CustomerDto): CustomerDto {
        val customer = customerRepository.findById(customerId)
            .orElseThrow { EntityNotFoundException(Customer::class.java, mutableMapOf("id" to customerId)) }
        customerDto.firstName?.let { customer.firstName = it }
        customerDto.lastName?.let { customer.lastName = it }
        customerDto.dateOfBirth?.let { customer.dateOfBirth = it }
        customerRepository.save(customer)
        return customer.toDto()
    }

    @Transactional
    fun deleteCustomer(customerId: Long) {
        val customer = customerRepository.findById(customerId)
            .orElseThrow { EntityNotFoundException(Customer::class.java, mutableMapOf("id" to customerId)) }
        customerRepository.delete(customer)
    }
}
