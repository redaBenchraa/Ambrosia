package com.ambrosia.nymph.exceptions

class OrderStatusConversionFailedException(val status: String, e: Exception) : RuntimeException()
