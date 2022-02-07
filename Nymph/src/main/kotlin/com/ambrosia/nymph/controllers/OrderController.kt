package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.constants.OrderStatus.APPROVED
import com.ambrosia.nymph.constants.OrderStatus.CONFIRMED
import com.ambrosia.nymph.dtos.AddOrderDto
import com.ambrosia.nymph.dtos.OrderDto
import com.ambrosia.nymph.services.OrderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("businesses/{businessId}/tables/{tableId}/sessions/{sessionId}/orders")
class OrderController(@Autowired private val orderService: OrderService) {
    @PostMapping
    fun createOrder(
        @PathVariable("businessId") businessId: Long,
        @PathVariable("tableId") tableId: Long,
        @PathVariable("sessionId") sessionId: Long,
        @Valid @RequestBody addOrderDto: AddOrderDto,
    ): OrderDto = orderService.createOrder(businessId, tableId, sessionId, addOrderDto)

    @PostMapping("{orderId}")
    fun addItemsToOrder(
        @PathVariable("businessId") businessId: Long,
        @PathVariable("tableId") tableId: Long,
        @PathVariable("sessionId") sessionId: Long,
        @PathVariable("orderId") orderId: Long,
        @Valid @RequestBody addOrderDto: AddOrderDto,
    ): OrderDto = orderService.addItemsToOrder(businessId, tableId, sessionId, orderId, addOrderDto)

    @DeleteMapping("{orderId}/items/{orderItemId}")
    fun removeItemFromOrder(
        @PathVariable("businessId") businessId: Long,
        @PathVariable("tableId") tableId: Long,
        @PathVariable("sessionId") sessionId: Long,
        @PathVariable("orderId") orderId: Long,
        @PathVariable("orderItemId") orderItemId: Long,
    ) = orderService.removeItemFromOrder(businessId, tableId, sessionId, orderId, orderItemId)

    @PutMapping("{orderId}/confirm")
    fun confirmOrder(
        @PathVariable("businessId") businessId: Long,
        @PathVariable("tableId") tableId: Long,
        @PathVariable("sessionId") sessionId: Long,
        @PathVariable("orderId") orderId: Long,
    ): OrderDto = orderService.updateOrderStatus(businessId, tableId, sessionId, orderId, CONFIRMED)

    @PutMapping("{orderId}/approve")
    fun approveOrder(
        @PathVariable("businessId") businessId: Long,
        @PathVariable("tableId") tableId: Long,
        @PathVariable("sessionId") sessionId: Long,
        @PathVariable("orderId") orderId: Long,
    ): OrderDto = orderService.updateOrderStatus(businessId, tableId, sessionId, orderId, APPROVED)

}
