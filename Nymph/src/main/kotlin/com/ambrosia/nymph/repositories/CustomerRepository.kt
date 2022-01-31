package com.ambrosia.nymph.repositories

import com.ambrosia.nymph.entities.Customer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository : JpaRepository<Customer, Long> {
    fun existsByEmail(email: String): Boolean
}
