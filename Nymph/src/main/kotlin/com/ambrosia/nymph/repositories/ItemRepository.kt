package com.ambrosia.nymph.repositories

import com.ambrosia.nymph.entities.Item
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ItemRepository : JpaRepository<Item, Long> {
    fun findByBusinessId(businessId: Long): List<Item>
}
