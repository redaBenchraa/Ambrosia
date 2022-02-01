package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.dtos.SessionDto
import com.ambrosia.nymph.services.SessionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("businesses/{businessId}/tables/{tableId}/sessions")
class SessionController(@Autowired private val sessionService: SessionService) {

    @GetMapping
    fun getCurrentSession(
        @PathVariable("businessId") businessId: Long,
        @PathVariable("tableId") tableId: Long,
    ): SessionDto {
        return sessionService.getCurrentSession(businessId, tableId)
    }

    @PutMapping("{sessionId}")
    fun markAsClosed(
        @PathVariable("businessId") businessId: Long,
        @PathVariable("tableId") tableId: Long,
        @PathVariable("sessionId") sessionId: Long,
    ): SessionDto {
        return sessionService.markAsClosed(businessId, tableId, sessionId)
    }
}
