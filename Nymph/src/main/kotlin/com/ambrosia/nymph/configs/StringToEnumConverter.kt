package com.ambrosia.nymph.configs

import com.ambrosia.nymph.constants.OrderStatus
import org.springframework.core.convert.converter.Converter

class StringToEnumConverter : Converter<String, OrderStatus> {
    override fun convert(source: String): OrderStatus = OrderStatus.valueOf(source.uppercase())
}
