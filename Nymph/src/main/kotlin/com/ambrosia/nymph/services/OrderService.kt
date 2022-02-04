package com.ambrosia.nymph.services

import com.ambrosia.nymph.dtos.AddOrderDto
import com.ambrosia.nymph.dtos.ItemsToOrder
import com.ambrosia.nymph.dtos.OrderDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Item
import com.ambrosia.nymph.entities.Order
import com.ambrosia.nymph.entities.OrderItem
import com.ambrosia.nymph.entities.Session
import com.ambrosia.nymph.entities.Table
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.exceptions.SessionClosedException
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.repositories.BusinessRepository
import com.ambrosia.nymph.repositories.ItemRepository
import com.ambrosia.nymph.repositories.OrderItemRepository
import com.ambrosia.nymph.repositories.OrderRepository
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
    @Autowired private val orderItemRepository: OrderItemRepository,
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
        var orderItems = buildOrderItems(addOrderDto.items, order)
        orderItems = orderItemRepository.saveAll(orderItems)
        order.orderItems = orderItems.toMutableSet()
        return order.toDto()
    }

    @Transactional
    fun addItemsToOrder(
        businessId: Long, tableId: Long, sessionId: Long, orderId: Long, addOrderDto: AddOrderDto,
    ): OrderDto {
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
        val order = orderRepository.findById(orderId)
            .orElseThrow { EntityNotFoundException(Order::class.java, mutableMapOf("id" to orderId)) }
        var orderItems = buildOrderItems(addOrderDto.items, order)
        orderItems = orderItemRepository.saveAll(orderItems)
        order.orderItems.addAll(orderItems)
        return order.toDto()
    }

    @Transactional
    fun removeItemFromOrder(businessId: Long, tableId: Long, sessionId: Long, orderId: Long, orderItemId: Long) {
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
        if (!orderRepository.existsById(orderId)) {
            throw EntityNotFoundException(Order::class.java, mutableMapOf("id" to orderId))
        }
        if (!orderItemRepository.existsById(orderItemId)) {
            throw EntityNotFoundException(OrderItem::class.java, mutableMapOf("id" to orderItemId))
        }
        orderItemRepository.deleteById(orderItemId)
    }

    private fun buildOrderItems(orderItems: Set<ItemsToOrder>, order: Order): List<OrderItem> {
        return orderItems.stream().map {
            val item = itemRepository.findById(it.id)
                .orElseThrow { EntityNotFoundException(Item::class.java, mutableMapOf("id" to it.id)) }
            OrderItem(order = order,
                item = item,
                name = item.name,
                price = item.price,
                description = it.description ?: item.description)
        }.toList()
    }
}
