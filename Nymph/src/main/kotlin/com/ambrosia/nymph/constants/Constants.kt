package com.ambrosia.nymph.constants

class Constants private constructor() {
	init {
		throw AssertionError()
	}

	companion object {
		const val EXTRA_MIN: Long = 0L
		const val PRICE_MIN: Long = 0L
		const val NAME_MAX_SIZE = 48
		const val EMAIL_MAX_SIZE = 254
		const val NOW = "now()"
		const val UNDEFINED = "undefined"
	}
}
