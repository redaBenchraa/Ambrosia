package com.ambrosia.nymph.repositories

import com.ambrosia.nymph.entities.Menu
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MenuRepository : JpaRepository<Menu, Long> {
    fun findByBusinessId(businessId: Long): List<Menu>
}
