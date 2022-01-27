package com.ambrosia.nymph.repositories

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestExecutionListeners(
    listeners = [DependencyInjectionTestExecutionListener::class, TransactionDbUnitTestExecutionListener::class]
)
@DatabaseSetup("classpath:business.xml")
@ActiveProfiles("test")
class EmployeeRepositoryTest {

    private val businessId: Long = 1000

    @Autowired
    private lateinit var employeeRepository: EmployeeRepository

    @Test
    fun `Find employees by business id`() {
        assertEquals(1, employeeRepository.findByBusinessId(businessId).size)
    }

    @Test
    fun `Check if employee exists by email`() {
        assertTrue(employeeRepository.existsByEmail("email@email.com"))
        assertFalse(employeeRepository.existsByEmail("email2@email.com"))
    }
}
