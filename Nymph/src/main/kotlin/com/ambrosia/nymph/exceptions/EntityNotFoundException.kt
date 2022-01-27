package com.ambrosia.nymph.exceptions

class EntityNotFoundException(val entityClass: Class<*>, val parameters: MutableMap<String, Any>) :
    RuntimeException()