package com.ambrosia.nymph.models


class KeycloakUser(
    val id: String,
    val username: String,
    val password: String,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val roles: List<String>?,
)