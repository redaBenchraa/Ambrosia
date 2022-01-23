package com.ambrosia.nymph.repositories

import com.ambrosia.nymph.entities.MenuItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MenuItemRepository : JpaRepository<MenuItem, Long>
