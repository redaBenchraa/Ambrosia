package com.ambrosia.nymph

import com.ambrosia.nymph.services.MockUserService
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
internal class NymphApplicationKtTest {

    @Autowired
    lateinit var mockUserService: MockUserService

    @Test
    fun `Load context with test profile`() {
        assertNotNull(mockUserService)
    }
}