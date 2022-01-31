package com.ambrosia.nymph.services

import com.ambrosia.nymph.dtos.CustomerRegistrationDto
import com.ambrosia.nymph.dtos.EditEmailDto
import com.ambrosia.nymph.entities.Customer
import com.ambrosia.nymph.exceptions.EntityAlreadyExistsException
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.exceptions.KeycloakException
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.repositories.CustomerRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.util.Optional

class CustomerServiceTest {

    private val customerRepository: CustomerRepository = mockk()
    private val userService: UserService = mockk()
    private val customerService = CustomerService(customerRepository, userService)

    @Test
    fun `Add a new customer`() {
        every { userService.verifyThatEmailDoesNotExists(any()) } returns Unit
        every { customerRepository.save(any()) } returns getCustomer()
        every { userService.createKeycloakUser(any()) } returns Unit
        customerService.addCustomer(getCustomerRegistrationDto())
        verify {
            customerRepository.save(any())
            userService.createKeycloakUser(any())
        }
    }

    @Test
    fun `Add an customer with keycloak exception`() {
        every { userService.verifyThatEmailDoesNotExists(any()) } returns Unit
        every { customerRepository.save(any()) } returns getCustomer()
        every { userService.createKeycloakUser(any()) } throws KeycloakException(message = "error.keycloak.createUser")
        assertThrows<KeycloakException> {
            customerService.addCustomer(getCustomerRegistrationDto())
        }
    }

    @Test
    fun `Add an customer to a business with an existing email`() {
        every { userService.verifyThatEmailDoesNotExists(any()) } throws EntityAlreadyExistsException(Customer::class.java,
            mutableMapOf())
        assertThrows<EntityAlreadyExistsException> {
            customerService.addCustomer(getCustomerRegistrationDto())
        }
    }

    @Test
    fun `Edit an customer`() {
        every { customerRepository.findById(any()) } returns Optional.of(getCustomer())
        every { customerRepository.save(any()) } returns getCustomer()
        val customer = getCustomer().toDto().apply {
            firstName = "new firstName"
            lastName = "new lastName"
        }
        val result = customerService.editCustomer(1, customer)
        assertEquals("new firstName", result.firstName)
        assertEquals("new lastName", result.lastName)
        verify {
            customerRepository.findById(any())
            customerRepository.save(any())
        }
    }

    @Test
    fun `Edit an customer with null values`() {
        every { customerRepository.findById(any()) } returns Optional.of(getCustomer())
        every { customerRepository.save(any()) } returns getCustomer()
        val customer = getCustomer().toDto().apply {
            firstName = null
            lastName = null
        }
        val result = customerService.editCustomer(1, customer)
        assertEquals("firstName", result.firstName)
        assertEquals("lastName", result.lastName)
        verify {
            customerRepository.findById(any())
            customerRepository.save(any())
        }
    }

    @Test
    fun `Edit an customer email`() {
        every { customerRepository.findById(any()) } returns Optional.of(getCustomer())
        every { userService.updateEmail(any()) } returns Unit
        every { customerRepository.save(any()) } returns getCustomer()
        val result = customerService.editCustomerEmail(1, EditEmailDto(email = "email2@email.com"))
        assertEquals("email2@email.com", result.email)
        verify {
            customerRepository.findById(any())
            userService.updateEmail(any())
            customerRepository.save(any())
        }
    }

    @Test
    fun `Edit a non existing customer`() {
        every { customerRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> {
            customerService.editCustomer(1, getCustomer().toDto())
        }
    }

    @Test
    fun `Remove an customer`() {
        every { customerRepository.findById(any()) } returns Optional.of(getCustomer())
        every { customerRepository.delete(any()) } returns Unit
        customerService.deleteCustomer(1)
        verify { customerRepository.delete(any()) }
    }

    private fun getCustomerRegistrationDto(): CustomerRegistrationDto =
        CustomerRegistrationDto(
            firstName = "firstName",
            lastName = "lastName",
            dateOfBirth = LocalDate.now(),
            password = "password",
            email = "email@email.com",
        )

    private fun getCustomer(): Customer =
        Customer(
            firstName = "firstName",
            lastName = "lastName",
            dateOfBirth = LocalDate.now(),
            email = "email@email.com"
        )
}
