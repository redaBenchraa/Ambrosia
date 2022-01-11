package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.services.BusinessService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("business")
class BusinessController {
	@Autowired
	lateinit var businessService: BusinessService
}