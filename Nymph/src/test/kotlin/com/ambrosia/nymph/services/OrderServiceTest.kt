package com.ambrosia.nymph.services

import com.ambrosia.nymph.dtos.AddOrderDto
import com.ambrosia.nymph.dtos.ItemsToOrder
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Item
import com.ambrosia.nymph.entities.Order
import com.ambrosia.nymph.entities.OrderedItem
import com.ambrosia.nymph.entities.Session
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.exceptions.SessionClosedException
import com.ambrosia.nymph.repositories.BusinessRepository
import com.ambrosia.nymph.repositories.ItemRepository
import com.ambrosia.nymph.repositories.OrderRepository
import com.ambrosia.nymph.repositories.OrderedItemRepository
import com.ambrosia.nymph.repositories.SessionRepository
import com.ambrosia.nymph.repositories.TableRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional

class OrderServiceTest {

    private val businessRepository: BusinessRepository = mockk(relaxed = true)
    private val tableRepository: TableRepository = mockk(relaxed = true)
    private val sessionRepository: SessionRepository = mockk(relaxed = true)
    private val itemRepository: ItemRepository = mockk(relaxed = true)
    private val orderRepository: OrderRepository = mockk(relaxed = true)
    private val orderItemRepository: OrderedItemRepository = mockk(relaxed = true)
    private val orderService = OrderService(businessRepository,
        tableRepository,
        sessionRepository,
        itemRepository,
        orderRepository,
        orderItemRepository)

    @Test
    fun `Create new order`() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns true
        every { sessionRepository.findById(any()) } returns Optional.of(getSession())
        every { itemRepository.findById(any()) } returns Optional.of(getItem())
        every { orderRepository.save(any()) } returns getOrder()
        every { orderItemRepository.saveAll(any<Iterable<OrderedItem>>()) } returns listOf(getOrderedItem())
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

    private fun getOrder(): Order = Order(session = getSession())

    private fun getOrderedItem(): OrderedItem =
        OrderedItem(order = getOrder(), description = "description", name = "name", price = 10.0, item = getItem())

    private fun getSession(): Session =
        Session(approved = true,
            closed = false,
            paid = false,
            business = getBusiness())

    private fun getItem(): Item =
        Item(name = "name",
            description = "description",
            image = "image",
            price = 10.0,
            onlyForMenu = true,
            business = getBusiness())

    private fun getBusiness() = Business(name = "name", email = "email", phoneNumber = "phoneNumber")

}
