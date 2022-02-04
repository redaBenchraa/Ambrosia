package com.ambrosia.nymph.services

import com.ambrosia.nymph.dtos.AddOrderDto
import com.ambrosia.nymph.dtos.OrderDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Item
import com.ambrosia.nymph.entities.Order
import com.ambrosia.nymph.entities.OrderedItem
import com.ambrosia.nymph.entities.Session
import com.ambrosia.nymph.entities.Table
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.exceptions.SessionClosedException
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.repositories.BusinessRepository
import com.ambrosia.nymph.repositories.ItemRepository
import com.ambrosia.nymph.repositories.OrderRepository
import com.ambrosia.nymph.repositories.OrderedItemRepository
import com.ambrosia.nymph.repositories.SessionRepository
import com.ambrosia.nymph.repositories.TableRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class OrderService(
    @Autowired private val businessRepository: BusinessRepository,
    @Autowired private val tableRepository: TableRepository,
    @Autowired private val sessionRepository: SessionRepository,
    @Autowired private val itemRepository: ItemRepository,
    @Autowired private val orderRepository: OrderRepository,
    @Autowired private val orderItemRepository: OrderedItemRepository,
) {
    @Transactional
    fun createOrder(businessId: Long, tableId: Long, sessionId: Long, addOrderDto: AddOrderDto): OrderDto {
        if (!businessRepository.existsById(businessId)) {
            throw EntityNotFoundException(Business::class.java, mutableMapOf("id" to businessId))
        }
        if (!tableRepository.existsById(tableId)) {
            throw EntityNotFoundException(Table::class.java, mutableMapOf("id" to tableId))
        }
        val session = sessionRepository.findById(sessionId)
            .orElseThrow { EntityNotFoundException(Session::class.java, mutableMapOf("id" to sessionId)) }
        if (session.closed) {
            throw SessionClosedException(mutableMapOf("id" to sessionId))
        }
        val order = Order(session = session)
        orderRepository.save(order).let { order.id = it.id }
        var orderedItems = addOrderDto.items.stream().map {
            val item = itemRepository.findById(it.id)
                .orElseThrow { EntityNotFoundException(Item::class.java, mutableMapOf("id" to it.id)) }
            OrderedItem(order = order, item = item, name = item.name, price = item.price, description = it.description)
        }.toList()
        orderedItems = orderItemRepository.saveAll(orderedItems)
        order.orderedItems = orderedItems.toMutableSet()
        return order.toDto()
    }
}
