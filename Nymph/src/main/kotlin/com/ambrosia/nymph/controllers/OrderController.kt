package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.dtos.AddOrderDto
import com.ambrosia.nymph.dtos.OrderDto
import com.ambrosia.nymph.services.OrderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
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
    ): OrderDto {
        return orderService.createOrder(businessId, tableId, sessionId, addOrderDto)
    }
}