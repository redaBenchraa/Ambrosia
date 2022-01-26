package com.ambrosia.nymph.models

data class KeycloakUser(
    val id: String? = null,
    val username: String,
    val password: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val roles: List<String>,
)
