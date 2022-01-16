package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.dtos.BusinessRegistrationDto
import com.ambrosia.nymph.services.BusinessService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("business")
class BusinessController(@Autowired private val businessService: BusinessService) {
	@PostMapping
	fun createBusiness(@RequestBody business: BusinessRegistrationDto): BusinessRegistrationDto {
		return businessService.createBusiness(business)
	}
}