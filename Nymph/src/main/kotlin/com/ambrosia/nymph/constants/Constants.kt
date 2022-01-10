package com.ambrosia.nymph.constants

class Constants private constructor() {
    init {
        throw AssertionError()
    }

    companion object {
        const val PRICE_MIN: Long = 0L
        const val NAME_MIN_SIZE = 3
        const val NAME_MAX_SIZE = 48
        const val PASSWORD_MIN_SIZE = 6
        const val PASSWORD_MAX_SIZE = 254
        const val EMAIL_MAX_SIZE = 254
        const val EUR = "EUR"
        const val NOW = "now"
    }
}
