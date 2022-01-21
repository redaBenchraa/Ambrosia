package com.ambrosia.nymph.configs

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
import java.util.*
import javax.servlet.http.HttpServletRequest

@Configuration
class CustomLocaleResolver : AcceptHeaderLocaleResolver(), WebMvcConfigurer {
    final var locales = listOf(Locale("en"), Locale("fr"))

    override fun resolveLocale(request: HttpServletRequest): Locale {
        val headerLang = request.getHeader("Accept-Language")
        return if (Objects.isNull(headerLang) || headerLang.isEmpty()) Locale.getDefault()
        else Locale.lookup(Locale.LanguageRange.parse(headerLang), locales)
    }

    @Bean
    fun messageSource(): ResourceBundleMessageSource = ResourceBundleMessageSource().apply {
        setBasename("messages")
        setDefaultEncoding("windows-1252")
        setUseCodeAsDefaultMessage(true)
    }
}
