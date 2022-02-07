package com.ambrosia.nymph.repositories

import com.ambrosia.nymph.entities.Session
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SessionRepository : JpaRepository<Session, Long> {
    fun findFirstByTableIdOrderByUpdatedAtDescIdDesc(tableId: Long): Session?
}
