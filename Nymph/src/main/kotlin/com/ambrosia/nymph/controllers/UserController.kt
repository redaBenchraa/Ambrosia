package com.ambrosia.nymph.controllers

import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("users")
class UserController {

    @GetMapping("/me")
    fun loadUserDetail(authentication: KeycloakAuthenticationToken): MutableSet<String>? {
        return (authentication.details as SimpleKeycloakAccount).roles
    }
}
