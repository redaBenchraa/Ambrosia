package com.ambrosia.nymph.constants

class Urls private constructor() {
    init {
        throw AssertionError()
    }

    companion object {
        const val VIOLATIONS = "http://www.ambrosia.com/api/vionations"
        const val ENTITY_NOT_FOUND = "http://www.ambrosia.com/api/enitity-not-found"
        const val ENTITY_ALREADY_EXITS = "http://www.ambrosia.com/api/enitity-already-exists"
        const val KEYCLOAK = "http://www.ambrosia.com/api/keycloak"
    }
}
