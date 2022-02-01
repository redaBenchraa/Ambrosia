package com.ambrosia.nymph.services

import com.ambrosia.nymph.dtos.SessionDto
import com.ambrosia.nymph.entities.Bill
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Item
import com.ambrosia.nymph.entities.Order
import com.ambrosia.nymph.entities.OrderedItem
import com.ambrosia.nymph.entities.Session
import com.ambrosia.nymph.entities.Table
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.repositories.BusinessRepository
import com.ambrosia.nymph.repositories.SessionRepository
import com.ambrosia.nymph.repositories.TableRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional

class SessionServiceTest {

    private val businessRepository: BusinessRepository = mockk(relaxed = true)
    private val tableRepository: TableRepository = mockk(relaxed = true)
    private val sessionRepository: SessionRepository = mockk(relaxed = true)
    private val sessionService = SessionService(businessRepository, tableRepository, sessionRepository)

    @Test
    fun `Get current session`() {
        val session = Session(isPaid = false, isClosed = false, isApproved = false).apply { id = 1 }
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.findById(any()) } returns Optional.of(getTable())
        every { sessionRepository.findFirstByTableIdOrderByUpdatedAtDesc(any()) } returns session
        val result = sessionService.getCurrentSession(1, 1)
        assertEquals(1, result.id)
        verify {
            businessRepository.existsById(any())
            tableRepository.findById(any())
            sessionRepository.findFirstByTableIdOrderByUpdatedAtDesc(any())
        }
        verify(exactly = 0) {
            sessionRepository.save(any())
        }
    }

    @Test
    fun `Get current session from a non existing business`() {
        every { businessRepository.existsById(any()) } returns false
        assertThrows<EntityNotFoundException> { sessionService.getCurrentSession(1, 1) }
    }

    @Test
    fun `Get current session from a non existing table`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> { sessionService.getCurrentSession(1, 1) }
    }

    @Test
    fun `Create new session for paid last session`() {
        val session = Session(isPaid = true, isClosed = false, isApproved = true)
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.findById(any()) } returns Optional.of(getTable())
        every { sessionRepository.findFirstByTableIdOrderByUpdatedAtDesc(any()) } returns session
        every { sessionRepository.save(any()) } returns session.apply { id = 1 }
        val result = sessionService.getCurrentSession(1, 1)
        assertEquals(1, result.id)
        verify {
            businessRepository.existsById(any())
            tableRepository.findById(any())
            sessionRepository.findFirstByTableIdOrderByUpdatedAtDesc(any())
            sessionRepository.save(any())
        }
    }

    @Test
    fun `Create new session for closed last session`() {
        val session = Session(isPaid = false, isClosed = true, isApproved = true)
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.findById(any()) } returns Optional.of(getTable())
        every { sessionRepository.findFirstByTableIdOrderByUpdatedAtDesc(any()) } returns session
        every { sessionRepository.save(any()) } returns session.apply { id = 1 }
        val result = sessionService.getCurrentSession(1, 1)
        assertEquals(1, result.id)
        verify {
            businessRepository.existsById(any())
            tableRepository.findById(any())
            sessionRepository.findFirstByTableIdOrderByUpdatedAtDesc(any())
            sessionRepository.save(any())
        }
    }

    @Test
    fun `Create new session for non existing last session`() {
        val session = Session(isPaid = false, isClosed = true, isApproved = true)
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.findById(any()) } returns Optional.of(getTable())
        every { sessionRepository.findFirstByTableIdOrderByUpdatedAtDesc(any()) } returns null
        every { sessionRepository.save(any()) } returns session.apply { id = 1 }
        val result = sessionService.getCurrentSession(1, 1)
        assertEquals(1, result.id)
        verify {
            businessRepository.existsById(any())
            tableRepository.findById(any())
            sessionRepository.findFirstByTableIdOrderByUpdatedAtDesc(any())
            sessionRepository.save(any())
        }
    }

    @Test
    fun `Check if session is paid`() {
        val item = Item(name = "name", price = 10.0, business = getBusiness())
        val order = Order(session = Session(), orderedItems = mutableSetOf())
        val orderedItem = OrderedItem(name = "name", price = 10.0, order = order, item = item)
        order.orderedItems.add(orderedItem)
        val session = Session(isPaid = false, orders = mutableSetOf(order), bills = mutableSetOf(Bill(amount = 10.0)))
        assertTrue(sessionService.checkIfSessionIsPaid(session))
    }

    @Test
    fun `Check if session is paid when sums are not equal`() {
        val item = Item(name = "name", price = 10.0, business = getBusiness())
        val order = Order(session = Session(), orderedItems = mutableSetOf())
        val orderedItem = OrderedItem(name = "name", price = 10.0, order = order, item = item)
        order.orderedItems.add(orderedItem)
        val session = Session(isPaid = false,
            orders = mutableSetOf(order),
            bills = mutableSetOf(Bill(amount = 5.0), Bill(amount = 1.0)))
        assertFalse(sessionService.checkIfSessionIsPaid(session))
    }

    @Test
    fun `Check if session is paid with no orders`() {
        assertFalse(sessionService.checkIfSessionIsPaid(Session(isPaid = false)))
    }

    @Test
    fun `Check if session is paid with no bill`() {
        val order = Order(session = Session())
        val session = Session(isPaid = false, orders = mutableSetOf(order))
        assertFalse(sessionService.checkIfSessionIsPaid(session))
    }

    @Test
    fun `Edit session`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns true
        every { sessionRepository.findById(any()) } returns Optional.of(getSession())
        every { sessionRepository.save(any()) } returns getSession()
        val result = sessionService.editSession(1, 1, 1, SessionDto(isPaid = true, isApproved = false, isClosed = true))
        assertTrue(result.isPaid ?: true)
        assertFalse(result.isApproved ?: false)
        assertTrue(result.isClosed ?: true)
        verify {
            businessRepository.existsById(any())
            tableRepository.existsById(any())
            sessionRepository.findById(any())
            sessionRepository.save(any())
        }
    }

    @Test
    fun `Edit session for a non existing business`() {
        every { businessRepository.existsById(any()) } returns false
        every { tableRepository.existsById(any()) } returns true
        every { sessionRepository.findById(any()) } returns Optional.of(getSession())
        every { sessionRepository.save(any()) } returns getSession()
        assertThrows<EntityNotFoundException> { sessionService.editSession(1, 1, 1, SessionDto(isPaid = true)) }
        verify(exactly = 0) {
            sessionRepository.save(any())
        }
    }

    @Test
    fun `Edit session for a non existing table`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns false
        every { sessionRepository.findById(any()) } returns Optional.of(getSession())
        every { sessionRepository.save(any()) } returns getSession()
        assertThrows<EntityNotFoundException> { sessionService.editSession(1, 1, 1, SessionDto(isPaid = true)) }
        verify(exactly = 0) {
            sessionRepository.save(any())
        }
    }

    @Test
    fun `Edit session for a non existing session`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns true
        every { sessionRepository.findById(any()) } returns Optional.empty()
        every { sessionRepository.save(any()) } returns getSession()
        assertThrows<EntityNotFoundException> { sessionService.editSession(1, 1, 1, SessionDto(isPaid = true)) }
        verify(exactly = 0) {
            sessionRepository.save(any())
        }
    }

    private fun getSession(): Session =
        Session(isApproved = true, isClosed = false, isPaid = false, business = getBusiness())

    private fun getTable(): Table =
        Table(number = 1, business = getBusiness())

    private fun getBusiness(): Business =
        Business(name = "name", email = "email", phoneNumber = "phoneNumber")

}
