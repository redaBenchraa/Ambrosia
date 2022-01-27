package com.ambrosia.nymph.exceptions

class EntityAlreadyExistsException(val entityClass: Class<*>, val parameters: MutableMap<String, Any>) :
    RuntimeException()
