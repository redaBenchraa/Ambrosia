package com.ambrosia.nymph.entities

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class SessionTest {
    @Test
    fun `Equality test`() {
        val session = Session(isApproved = false, isPaid = false).apply { id = 1 }
        val session1 = Session(isApproved = false, isPaid = false).apply { id = 1 }
        val session2 = Session(isApproved = false, isPaid = false).apply { id = 2 }
        val table = Table(number = 1, business = Business("name", "phone", "email")).apply { id = 1 }
        assertEquals(session, session)
        assertEquals(session, session1)
        assertNotEquals(session, session2)
        assertNotEquals(session, null)
        assertNotEquals(session, table)
    }
}
