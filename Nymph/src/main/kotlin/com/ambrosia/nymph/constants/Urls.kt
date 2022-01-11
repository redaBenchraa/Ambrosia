package com.ambrosia.nymph.constants

class Urls private constructor() {
	init {
		throw AssertionError()
	}

	companion object {
		const val VIOLATIONS = "http://www.ambrosia.com/api/vionations"
		const val NOT_FOUND = "http://www.ambrosia.com/api/enitity-not-found"
		const val ALREADY_EXITS =
			"http://www.ambrosia.com/api/enitity-already_exists"
		const val KEYCLOAK = "http://www.ambrosia.com/api/keycloak"
	}
}