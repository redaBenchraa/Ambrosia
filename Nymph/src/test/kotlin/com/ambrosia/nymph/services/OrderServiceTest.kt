package com.ambrosia.nymph.services

import com.ambrosia.nymph.dtos.AddOrderDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Item
import com.ambrosia.nymph.entities.Session
import com.ambrosia.nymph.repositories.BusinessRepository
import com.ambrosia.nymph.repositories.ItemRepository
import com.ambrosia.nymph.repositories.OrderRepository
import com.ambrosia.nymph.repositories.OrderedItemRepository
import com.ambrosia.nymph.repositories.SessionRepository
import com.ambrosia.nymph.repositories.TableRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.util.Optional

class OrderServiceTest {

    private val businessRepository: BusinessRepository = mockk(relaxed = true)
    private val tableRepository: TableRepository = mockk(relaxed = true)
    private val sessionRepository: SessionRepository = mockk(relaxed = true)
    private val itemRepository: ItemRepository = mockk(relaxed = true)
    private val orderRepository: OrderRepository = mockk(relaxed = true)
    private val orderedItemRepository: OrderedItemRepository = mockk(relaxed = true)
    private val orderService = OrderService(businessRepository,
        tableRepository,
        sessionRepository,
        itemRepository,
        orderRepository,
        orderedItemRepository)

    @Test
    fun createOrder() {
        every { businessRepository.existsById(any()) } returns true
        every { tableRepository.existsById(any()) } returns true
        every { sessionRepository.findById(any()) } returns Optional.of(getSession())
        every { itemRepository.findById(any()) } returns Optional.of(getItem())
        val result = orderService.createOrder(1, 1, 1, AddOrderDto(items = mutableSetOf()))
    }

    private fun getSession(): Session =
        Session(isApproved = true,
            isClosed = false,
            isPaid = false,
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