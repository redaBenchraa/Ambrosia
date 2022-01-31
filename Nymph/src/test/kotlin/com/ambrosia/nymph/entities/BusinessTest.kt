package com.ambrosia.nymph.entities

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class BusinessTest {
    @Test
    fun `Equality test`() {
        val business = Business(name = "name", phoneNumber = "", email = "email").apply { id = 1 }
        val business1 = Business(name = "name", phoneNumber = "", email = "email").apply { id = 1 }
        val business2 = Business(name = "name", phoneNumber = "", email = "email").apply { id = 2 }
        assertEquals(business, business)
        assertEquals(business, business1)
        assertNotEquals(business, business2)
        assertNotEquals(business, null)
        assertNotEquals(business, Table(number = 1).apply { id = 1 })
    }
}
