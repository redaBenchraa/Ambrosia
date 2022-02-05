package com.ambrosia.nymph.services

import com.ambrosia.nymph.dtos.AddOrderDto
import com.ambrosia.nymph.dtos.ItemsToOrder
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Item
import com.ambrosia.nymph.entities.Order
import com.ambrosia.nymph.entities.OrderItem
import com.ambrosia.nymph.entities.Session
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.exceptions.SessionClosedException
import com.ambrosia.nymph.repositories.BusinessRepository
import com.ambrosia.nymph.repositories.ItemRepository
import com.ambrosia.nymph.repositories.OrderItemRepository
import com.ambrosia.nymph.repositories.OrderRepository
import com.ambrosia.nymph.repositories.SessionRepository
import com.ambrosia.nymph.repositories.TableRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.util.Optional

class OrderServiceTest {

    private val businessRepository: BusinessRepository = mockk(relaxed = true)
    private val tableRepository: TableRepository = mockk(relaxed = true)
    private val sessionRepository: SessionRepository = mockk(relaxed = true)
    private val itemRepository: ItemRepository = mockk(relaxed = true)
    private val orderRepository: OrderRepository = mockk(relaxed = true)
    private val orderItemRepository: OrderItemRepository = mockk(relaxed = true)
    private val orderService = OrderService(
        businessRepository,
        tableRepository,
        sessionRepository,
        itemRepository,
        orderRepository,
        orderItemRepository
    )

    @Test
    fun `Create new order`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns true
        every { sessionRepository.findById(any()) } returns Optional.of(getSession())
        every { orderRepository.save(any()) } returns getOrder()
        every { itemRepository.findById(any()) } returns Optional.of(getItem())
        every { orderItemRepository.saveAll(any<Iterable<OrderItem>>()) } returns listOf(getOrderedItem())
        val addOrderDto = AddOrderDto(items = mutableSetOf(ItemsToOrder(id = 1, description = "description")))
        val result = orderService.createOrder(1, 1, 1, addOrderDto)
        assertNotNull(result)
        assertEquals(1, result.orderItems.size)
        assertEquals("name", result.orderItems.first().name)
        assertEquals("description", result.orderItems.first().description)
    }

    @Test
    fun `Create new order for a non existing business`() {
        every { businessRepository.existsById(any()) } returns false
        assertThrows<EntityNotFoundException> { orderService.createOrder(1, 1, 1, AddOrderDto()) }
    }

    @Test
    fun `Create new order for a non existing table`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns false
        assertThrows<EntityNotFoundException> { orderService.createOrder(1, 1, 1, AddOrderDto()) }
    }

    @Test
    fun `Create new order for a non existing session`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns true
        every { sessionRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> { orderService.createOrder(1, 1, 1, AddOrderDto()) }
    }

    @Test
    fun `Create new order with a closed session`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns true
        every { sessionRepository.findById(any()) } returns Optional.of(getSession().apply { closed = true })
        every { itemRepository.findById(any()) } returns Optional.empty()
        assertThrows<SessionClosedException> { orderService.createOrder(1, 1, 1, AddOrderDto()) }
        verify(exactly = 0) { orderRepository.save(any()) }
    }

    @Test
    fun `Create new order with a non existing item`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns true
        every { sessionRepository.findById(any()) } returns Optional.of(getSession())
        every { itemRepository.findById(any()) } returns Optional.empty()
        every { orderRepository.save(any()) } returns getOrder()
        val addOrderDto = AddOrderDto(items = mutableSetOf(ItemsToOrder(id = 1, description = "description")))
        assertThrows<EntityNotFoundException> { orderService.createOrder(1, 1, 1, addOrderDto) }
    }

    @Test
    fun `Remove order item from order`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns true
        every { sessionRepository.findById(any()) } returns Optional.of(getSession())
        every { orderRepository.existsById(any()) } returns true
        every { orderItemRepository.existsById(any()) } returns true
        every { orderItemRepository.deleteById(any()) } returns Unit
        assertDoesNotThrow { orderService.removeItemFromOrder(1, 1, 1, 1, 1) }
        verify { orderItemRepository.deleteById(any()) }
    }

    @Test
    fun `Remove order item from order from a non existing business`() {
        every { businessRepository.existsById(any()) } returns false
        assertThrows<EntityNotFoundException> { orderService.removeItemFromOrder(1, 1, 1, 1, 1) }
        verify(exactly = 0) { orderItemRepository.deleteById(any()) }
    }

    @Test
    fun `Remove order item from order from a non existing table `() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns false
        assertThrows<EntityNotFoundException> { orderService.removeItemFromOrder(1, 1, 1, 1, 1) }
        verify(exactly = 0) { orderItemRepository.deleteById(any()) }
    }

    @Test
    fun `Remove order item from order from a non existing session`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns true
        every { sessionRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> { orderService.removeItemFromOrder(1, 1, 1, 1, 1) }
        verify(exactly = 0) { orderItemRepository.deleteById(any()) }
    }

    @Test
    fun `Remove order item from order from a closed session`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns true
        every { sessionRepository.findById(any()) } returns Optional.of(getSession().apply { closed = true })
        assertThrows<SessionClosedException> { orderService.removeItemFromOrder(1, 1, 1, 1, 1) }
        verify(exactly = 0) { orderItemRepository.deleteById(any()) }
    }

    @Test
    fun `Remove order item from non existing order`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns true
        every { sessionRepository.findById(any()) } returns Optional.of(getSession())
        every { orderRepository.existsById(any()) } returns false
        assertThrows<EntityNotFoundException> { orderService.removeItemFromOrder(1, 1, 1, 1, 1) }
        verify(exactly = 0) { orderItemRepository.deleteById(any()) }
    }

    @Test
    fun `Remove a non existing order item`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns true
        every { sessionRepository.findById(any()) } returns Optional.of(getSession())
        every { orderRepository.existsById(any()) } returns true
        every { orderItemRepository.existsById(any()) } returns false
        assertThrows<EntityNotFoundException> { orderService.removeItemFromOrder(1, 1, 1, 1, 1) }
        verify(exactly = 0) { orderItemRepository.deleteById(any()) }
    }

    @Test
    fun `Add items to order`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns true
        every { sessionRepository.findById(any()) } returns Optional.of(getSession())
        every { itemRepository.findById(any()) } returns Optional.of(getItem())
        every { orderRepository.findById(any()) } returns Optional.of(getOrder())
        every { orderItemRepository.saveAll(any<Iterable<OrderItem>>()) } returns listOf(getOrderedItem())
        val addOrderDto = AddOrderDto(items = mutableSetOf(ItemsToOrder(id = 1, description = "description")))
        val result = orderService.addItemsToOrder(1, 1, 1, 1, addOrderDto)
        assertNotNull(result)
        assertEquals(1, result.orderItems.size)
        assertEquals("name", result.orderItems.first().name)
        assertEquals("description", result.orderItems.first().description)
        verify { orderItemRepository.saveAll(any<Iterable<OrderItem>>()) }
    }

    @Test
    fun `Add items to order of a non existing business`() {
        every { businessRepository.existsById(any()) } returns false
        assertThrows<EntityNotFoundException> { orderService.addItemsToOrder(1, 1, 1, 1, AddOrderDto()) }
        verify(exactly = 0) { orderItemRepository.saveAll(any<Iterable<OrderItem>>()) }
    }

    @Test
    fun `Add items to order of a non existing table`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns false
        assertThrows<EntityNotFoundException> { orderService.addItemsToOrder(1, 1, 1, 1, AddOrderDto()) }
        verify(exactly = 0) { orderItemRepository.saveAll(any<Iterable<OrderItem>>()) }
    }

    @Test
    fun `Add items to order of a non existing session`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns true
        every { sessionRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> { orderService.addItemsToOrder(1, 1, 1, 1, AddOrderDto()) }
        verify(exactly = 0) { orderItemRepository.saveAll(any<Iterable<OrderItem>>()) }
    }

    @Test
    fun `Add items to order of a closed session`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns true
        every { sessionRepository.findById(any()) } returns Optional.of(getSession().apply { closed = true })
        assertThrows<SessionClosedException> { orderService.addItemsToOrder(1, 1, 1, 1, AddOrderDto()) }
        verify(exactly = 0) { orderItemRepository.saveAll(any<Iterable<OrderItem>>()) }
    }


    @Test
    fun `Add items to order of a non existing order`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns true
        every { sessionRepository.findById(any()) } returns Optional.of(getSession())
        every { orderRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> { orderService.addItemsToOrder(1, 1, 1, 1, AddOrderDto()) }
        verify(exactly = 0) { orderItemRepository.saveAll(any<Iterable<OrderItem>>()) }
    }

    @Test
    fun `Add a non existing item to order`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns true
        every { sessionRepository.findById(any()) } returns Optional.of(getSession())
        every { orderRepository.findById(any()) } returns Optional.of(getOrder())
        every { itemRepository.findById(any()) } returns Optional.empty()
        val addOrderDto = AddOrderDto(items = mutableSetOf(ItemsToOrder(id = 1, description = "description")))
        assertThrows<EntityNotFoundException> { orderService.addItemsToOrder(1, 1, 1, 1, addOrderDto) }
        verify(exactly = 0) { orderItemRepository.saveAll(any<Iterable<OrderItem>>()) }
    }

    @Test
    fun `Confirm order`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns true
        every { sessionRepository.findById(any()) } returns Optional.of(getSession())
        every { itemRepository.findById(any()) } returns Optional.of(getItem())
        every { orderRepository.findById(any()) } returns Optional.of(getOrder())
        every { orderRepository.save(any()) } returns getOrder().apply { confirmed = true }
        val result = orderService.confirmOrder(1, 1, 1, 1)
        assertTrue(result.confirmed)
        verify { orderRepository.save(any()) }
    }

    @Test
    fun `Confirm order of a non existing business`() {
        every { businessRepository.existsById(any()) } returns false
        assertThrows<EntityNotFoundException> { orderService.confirmOrder(1, 1, 1, 1) }
        verify(exactly = 0) { orderRepository.save(any()) }
    }

    @Test
    fun `Confirm order of a non existing table`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns false
        assertThrows<EntityNotFoundException> { orderService.confirmOrder(1, 1, 1, 1) }
        verify(exactly = 0) { orderRepository.save(any()) }
    }

    @Test
    fun `Confirm order of a non existing session`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns true
        every { sessionRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> { orderService.confirmOrder(1, 1, 1, 1) }
        verify(exactly = 0) { orderRepository.save(any()) }
    }

    @Test
    fun `Confirm order of a closed session`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns true
        every { sessionRepository.findById(any()) } returns Optional.of(getSession().apply { closed = true })
        assertThrows<SessionClosedException> { orderService.confirmOrder(1, 1, 1, 1) }
        verify(exactly = 0) { orderRepository.save(any()) }
    }

    @Test
    fun `Confirm order of a non existing order`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns true
        every { sessionRepository.findById(any()) } returns Optional.of(getSession())
        every { orderRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> { orderService.confirmOrder(1, 1, 1, 1) }
        verify(exactly = 0) { orderRepository.save(any()) }
    }

    @Test
    fun `Approve order`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns true
        every { sessionRepository.findById(any()) } returns Optional.of(getSession())
        every { itemRepository.findById(any()) } returns Optional.of(getItem())
        every { orderRepository.findById(any()) } returns Optional.of(getOrder())
        every { orderRepository.save(any()) } returns getOrder().apply { approved = true }
        val result = orderService.approveOrder(1, 1, 1, 1)
        assertTrue(result.approved)
        verify { orderRepository.save(any()) }
    }

    @Test
    fun `Approve order of a non existing business`() {
        every { businessRepository.existsById(any()) } returns false
        assertThrows<EntityNotFoundException> { orderService.approveOrder(1, 1, 1, 1) }
        verify(exactly = 0) { orderRepository.save(any()) }
    }

    @Test
    fun `Approve order of a non existing table`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns false
        assertThrows<EntityNotFoundException> { orderService.approveOrder(1, 1, 1, 1) }
        verify(exactly = 0) { orderRepository.save(any()) }
    }

    @Test
    fun `Approve order of a non existing session`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns true
        every { sessionRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> { orderService.approveOrder(1, 1, 1, 1) }
        verify(exactly = 0) { orderRepository.save(any()) }
    }

    @Test
    fun `Approve order of a closed session`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns true
        every { sessionRepository.findById(any()) } returns Optional.of(getSession().apply { closed = true })
        assertThrows<SessionClosedException> { orderService.approveOrder(1, 1, 1, 1) }
        verify(exactly = 0) { orderRepository.save(any()) }
    }

    @Test
    fun `Approve order of a non existing order`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns true
        every { sessionRepository.findById(any()) } returns Optional.of(getSession())
        every { orderRepository.findById(any()) } returns Optional.empty()
        assertThrows<EntityNotFoundException> { orderService.approveOrder(1, 1, 1, 1) }
        verify(exactly = 0) { orderRepository.save(any()) }
    }

    private fun getOrder(): Order = Order(session = getSession())

    private fun getOrderedItem(): OrderItem =
        OrderItem(order = getOrder(), description = "description", name = "name", price = 10.0, item = getItem())

    private fun getSession(): Session =
        Session(
            approved = true,
            closed = false,
            paid = false,
            business = getBusiness()
        )

    private fun getItem(): Item =
        Item(
            name = "name",
            description = "description",
            image = "image",
            price = 10.0,
            onlyForMenu = true,
            business = getBusiness()
        )

    private fun getBusiness() = Business(name = "name", email = "email", phoneNumber = "phoneNumber")

}
