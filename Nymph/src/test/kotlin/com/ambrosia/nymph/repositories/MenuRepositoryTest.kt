package com.ambrosia.nymph.repositories

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import org.junit.jupiter.api.Assertions.assertEquals
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
class MenuRepositoryTest {

    private val businessId: Long = 1000

    @Autowired
    private lateinit var menuRepository: MenuRepository

    @Test
    fun `Find menus by business id`() {
        assertEquals(1, menuRepository.findByBusinessId(businessId).size)
    }
}
