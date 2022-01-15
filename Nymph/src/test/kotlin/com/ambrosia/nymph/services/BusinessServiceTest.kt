package com.ambrosia.nymph.services

import com.ambrosia.nymph.repositories.BusinessRepository
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest()
class BusinessServiceTest {

	@Autowired
	lateinit var businessService: BusinessService

	@Mock
	lateinit var businessRepository: BusinessRepository

	@Test
	fun register() {
		assert(true).equals(true)
	}
}