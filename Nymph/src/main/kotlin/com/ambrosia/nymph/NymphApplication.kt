package com.ambrosia.nymph

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [ErrorMvcAutoConfiguration::class])
class NymphApplication

fun main(args: Array<String>) {
    runApplication<NymphApplication>(*args)
}
