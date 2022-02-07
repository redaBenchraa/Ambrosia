package com.ambrosia.nymph.configs

import com.ambrosia.nymph.constants.OrderStatus
import org.springframework.core.convert.converter.Converter

class StringToEnumConverter : Converter<String, OrderStatus> {
    override fun convert(source: String): OrderStatus? {
        return try {
            OrderStatus.valueOf(source.uppercase())
        } catch (e: IllegalArgumentException) {
            throw e
        }
    }
}
