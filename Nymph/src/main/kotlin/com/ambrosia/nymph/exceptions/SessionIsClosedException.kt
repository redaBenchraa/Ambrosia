package com.ambrosia.nymph.exceptions

class SessionIsClosedException(val parameters: MutableMap<String, Any>) :
    RuntimeException()
