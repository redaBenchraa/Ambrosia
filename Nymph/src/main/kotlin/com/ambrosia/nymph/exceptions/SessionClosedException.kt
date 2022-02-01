package com.ambrosia.nymph.exceptions

class SessionClosedException(val parameters: MutableMap<String, Any>) :
    RuntimeException()
