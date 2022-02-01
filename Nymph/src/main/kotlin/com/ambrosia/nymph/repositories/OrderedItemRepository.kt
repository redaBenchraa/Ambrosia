package com.ambrosia.nymph.repositories

import com.ambrosia.nymph.entities.OrderedItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderedItemRepository : JpaRepository<OrderedItem, Long> {
    fun findByOrderId(orderId: Long): List<OrderedItem>
}
