package com.ambrosia.nymph

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NymphApplication

fun main(args: Array<String>) {
	runApplication<NymphApplication>(*args)
}
