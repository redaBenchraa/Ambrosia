package com.ambrosia.nymph.constants

import com.ambrosia.nymph.constants.OrderStatus.APPROVED
import com.ambrosia.nymph.constants.OrderStatus.CANCELED
import com.ambrosia.nymph.constants.OrderStatus.CONFIRMED
import com.ambrosia.nymph.constants.OrderStatus.DELIVERED
import com.ambrosia.nymph.constants.OrderStatus.DRAFT
import com.ambrosia.nymph.constants.OrderStatus.ONGOING
import com.ambrosia.nymph.constants.OrderStatus.REJECTED
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class OrderStatusTest {

    @Test
    fun `Draft status`() {
        val status = DRAFT
        assertFalse(status.canChangeStatusTo(DRAFT))
        assertTrue(status.canChangeStatusTo(CONFIRMED))
        assertFalse(status.canChangeStatusTo(APPROVED))
        assertFalse(status.canChangeStatusTo(REJECTED))
        assertTrue(status.canChangeStatusTo(CANCELED))
        assertFalse(status.canChangeStatusTo(ONGOING))
        assertFalse(status.canChangeStatusTo(DELIVERED))
    }

    @Test
    fun `Confirmed status`() {
        val status = CONFIRMED
        assertFalse(status.canChangeStatusTo(DRAFT))
        assertFalse(status.canChangeStatusTo(CONFIRMED))
        assertTrue(status.canChangeStatusTo(APPROVED))
        assertTrue(status.canChangeStatusTo(REJECTED))
        assertTrue(status.canChangeStatusTo(CANCELED))
        assertFalse(status.canChangeStatusTo(ONGOING))
        assertFalse(status.canChangeStatusTo(DELIVERED))
    }

    @Test
    fun `Approved status`() {
        val status = APPROVED
        assertFalse(status.canChangeStatusTo(DRAFT))
        assertFalse(status.canChangeStatusTo(CONFIRMED))
        assertFalse(status.canChangeStatusTo(APPROVED))
        assertTrue(status.canChangeStatusTo(REJECTED))
        assertFalse(status.canChangeStatusTo(CANCELED))
        assertTrue(status.canChangeStatusTo(ONGOING))
        assertFalse(status.canChangeStatusTo(DELIVERED))
    }

    @Test
    fun `Rejected status`() {
        val status = REJECTED
        assertFalse(status.canChangeStatusTo(DRAFT))
        assertFalse(status.canChangeStatusTo(CONFIRMED))
        assertTrue(status.canChangeStatusTo(APPROVED))
        assertFalse(status.canChangeStatusTo(REJECTED))
        assertFalse(status.canChangeStatusTo(CANCELED))
        assertFalse(status.canChangeStatusTo(ONGOING))
        assertFalse(status.canChangeStatusTo(DELIVERED))
    }

    @Test
    fun `Canceled status`() {
        val status = CANCELED
        assertFalse(status.canChangeStatusTo(DRAFT))
        assertFalse(status.canChangeStatusTo(CONFIRMED))
        assertFalse(status.canChangeStatusTo(APPROVED))
        assertFalse(status.canChangeStatusTo(REJECTED))
        assertFalse(status.canChangeStatusTo(CANCELED))
        assertFalse(status.canChangeStatusTo(ONGOING))
        assertFalse(status.canChangeStatusTo(DELIVERED))
    }

    @Test
    fun `Ongoing status`() {
        val status = ONGOING
        assertFalse(status.canChangeStatusTo(DRAFT))
        assertFalse(status.canChangeStatusTo(CONFIRMED))
        assertFalse(status.canChangeStatusTo(APPROVED))
        assertFalse(status.canChangeStatusTo(REJECTED))
        assertFalse(status.canChangeStatusTo(CANCELED))
        assertFalse(status.canChangeStatusTo(ONGOING))
        assertTrue(status.canChangeStatusTo(DELIVERED))
    }

    @Test
    fun `Delivered status`() {
        val status = DELIVERED
        assertFalse(status.canChangeStatusTo(DRAFT))
        assertFalse(status.canChangeStatusTo(CONFIRMED))
        assertFalse(status.canChangeStatusTo(APPROVED))
        assertFalse(status.canChangeStatusTo(REJECTED))
        assertFalse(status.canChangeStatusTo(CANCELED))
        assertFalse(status.canChangeStatusTo(ONGOING))
        assertFalse(status.canChangeStatusTo(DELIVERED))
    }

}
