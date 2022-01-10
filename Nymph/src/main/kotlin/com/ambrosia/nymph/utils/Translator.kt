package com.ambrosia.nymph.utils

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.stereotype.Component

@Component
class Translator(private val messageSource: ResourceBundleMessageSource) {
    fun toLocale(msgCode: String): String {
        val locale = LocaleContextHolder.getLocale()
        return messageSource.getMessage(msgCode, null, locale)
    }
}
