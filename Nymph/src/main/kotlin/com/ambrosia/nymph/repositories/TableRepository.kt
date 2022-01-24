package com.ambrosia.nymph.repositories

import com.ambrosia.nymph.entities.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TableRepository : JpaRepository<Table, Long> {
    fun findByBusinessId(businessId: Long): List<Table>
}
