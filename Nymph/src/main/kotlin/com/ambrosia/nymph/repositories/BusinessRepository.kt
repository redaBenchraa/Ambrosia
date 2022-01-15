package com.ambrosia.nymph.repositories

import com.ambrosia.nymph.entities.Business
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BusinessRepository : JpaRepository<Business, String>
