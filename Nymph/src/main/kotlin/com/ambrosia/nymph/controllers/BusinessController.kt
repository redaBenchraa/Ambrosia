package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.dtos.BusinessRegistrationDto
import com.ambrosia.nymph.services.BusinessService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("businesses")
class BusinessController(@Autowired private val businessService: BusinessService) {

    @PostMapping("register")
    fun createBusiness(@Valid @RequestBody business: BusinessRegistrationDto): BusinessRegistrationDto {
        return businessService.createBusiness(business)
    }

    @PutMapping("{businessId}")
    fun editBusiness(
        @PathVariable("businessId") businessId: Long,
        @Valid @RequestBody business: BusinessRegistrationDto
    ): BusinessRegistrationDto {
        return businessService.editBusiness(businessId, business)
    }
}
