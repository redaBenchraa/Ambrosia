package com.ambrosia.nymph.configs

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.format.FormatterRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
import java.util.Locale
import java.util.Objects.isNull
import javax.servlet.http.HttpServletRequest

@Configuration
class WebMvcConfiguration : AcceptHeaderLocaleResolver(), WebMvcConfigurer {
    final var locales = listOf(Locale("en"), Locale("fr"))

    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverter(StringToEnumConverter())
    }

    override fun resolveLocale(request: HttpServletRequest): Locale {
        val headerLang = request.getHeader("Accept-Language")
        return if (isNull(headerLang) || headerLang.isEmpty()) Locale.getDefault()
        else Locale.lookup(Locale.LanguageRange.parse(headerLang), locales)
    }

    @Bean
    fun messageSource(): ResourceBundleMessageSource = ResourceBundleMessageSource().apply {
        setBasename("messages")
        setDefaultEncoding("windows-1252")
        setUseCodeAsDefaultMessage(true)
    }
}
