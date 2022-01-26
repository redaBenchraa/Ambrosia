package com.ambrosia.nymph.controllers

import com.ambrosia.nymph.exceptions.KeycloakException
import com.ambrosia.nymph.models.KeycloakUser
import com.ambrosia.nymph.services.AbstractUserService
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("users")
class UserController(@Autowired private val userService: AbstractUserService) {

    @GetMapping("/me")
    fun loadUserDetail(authentication: KeycloakAuthenticationToken): MutableSet<String>? {
        return (authentication.details as SimpleKeycloakAccount).roles
    }

    @PostMapping
    @Throws(KeycloakException::class)
    fun createUser() {
        userService.createKeycloakUser(user)
    }

    private val user: KeycloakUser
        get() =
            KeycloakUser(
                id = "",
                username = "a",
                email = "a",
                firstName = "a",
                lastName = "a",
                password = "a",
                roles = listOf("user")
            )
}
