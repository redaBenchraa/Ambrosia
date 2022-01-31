package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.dtos.CustomerDto
import com.ambrosia.nymph.dtos.CustomerRegistrationDto
import com.ambrosia.nymph.dtos.EditEmailDto
import com.ambrosia.nymph.services.CustomerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("customers")
class CustomerController(@Autowired private val customerService: CustomerService) {

    @PostMapping
    fun addCustomer(@Valid @RequestBody customer: CustomerRegistrationDto): CustomerDto {
        return customerService.addCustomer(customer)
    }

    @PutMapping("{customerId}")
    fun editCustomer(
        @PathVariable("customerId") customerId: Long,
        @Valid @RequestBody customer: CustomerDto,
    ): CustomerDto {
        return customerService.editCustomer(customerId, customer)
    }

    @DeleteMapping("{customerId}")
    fun deleteCustomer(@PathVariable("customerId") customerId: Long) {
        customerService.deleteCustomer(customerId)
    }

    @PutMapping("{customerId}/email")
    fun editCustomerEmail(
        @PathVariable("customerId") customerId: Long,
        @Valid @RequestBody editEmailDto: EditEmailDto,
    ): CustomerDto {
        return customerService.editCustomerEmail(customerId, editEmailDto)
    }

}
