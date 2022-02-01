package com.ambrosia.nymph.services

import com.ambrosia.nymph.dtos.SessionDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Session
import com.ambrosia.nymph.entities.Table
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.mappers.toDto
import com.ambrosia.nymph.repositories.BusinessRepository
import com.ambrosia.nymph.repositories.SessionRepository
import com.ambrosia.nymph.repositories.TableRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class SessionService(
    @Autowired private val businessRepository: BusinessRepository,
    @Autowired private val tableRepository: TableRepository,
    @Autowired private val sessionRepository: SessionRepository,
) {
    @Transactional
    fun getCurrentSession(businessId: Long, tableId: Long): SessionDto {
        businessRepository.findById(businessId)
            .orElseThrow { EntityNotFoundException(Business::class.java, mutableMapOf("id" to businessId)) }
        val table = tableRepository.findById(tableId)
            .orElseThrow { EntityNotFoundException(Table::class.java, mutableMapOf("id" to tableId)) }
        val lastSession = sessionRepository.findFirstByTableIdOrderByUpdatedAtDesc(tableId)
        if (lastSession == null || lastSession.isClosed || lastSession.isPaid) {
            val newSession = Session(table = table, isPaid = false, isClosed = false, isApproved = false)
            return sessionRepository.save(newSession).toDto()
        }
        return lastSession.toDto()
    }

    @Transactional
    fun editSession(businessId: Long, tableId: Long, sessionId: Long, sessionDto: SessionDto): SessionDto {
        businessRepository.findById(businessId)
            .orElseThrow { EntityNotFoundException(Business::class.java, mutableMapOf("id" to businessId)) }
        tableRepository.findById(tableId)
            .orElseThrow { EntityNotFoundException(Table::class.java, mutableMapOf("id" to tableId)) }
        val session = sessionRepository.findById(sessionId)
            .orElseThrow { EntityNotFoundException(Session::class.java, mutableMapOf("id" to sessionId)) }
        sessionDto.isApproved?.let { session.isApproved = it }
        sessionDto.isClosed?.let { session.isClosed = it }
        sessionDto.isPaid?.let { session.isPaid = it }
        sessionRepository.save(session)
        return session.toDto()
    }

    fun checkIfSessionIsPaid(session: Session): Boolean {
        if (session.orders.size == 0 || session.bills.size == 0) {
            return false
        }
        val total = session.orders.stream()
            .map { order -> order.orderedItem.stream().map { it.price }.toList().sum() }
            .toList().sum()
        val paySum = session.bills.stream().map { it.amount }.toList().sum()
        return paySum >= total
    }
}
