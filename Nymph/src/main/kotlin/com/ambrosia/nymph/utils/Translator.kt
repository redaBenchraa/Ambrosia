package com.ambrosia.nymph.utils

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.stereotype.Component

@Component
class Translator(private val messageSource: ResourceBundleMessageSource) {
    fun toLocale(msgCode: String?): String =
        if (msgCode == null) String()
        else messageSource.getMessage(msgCode, null, LocaleContextHolder.getLocale())
}
