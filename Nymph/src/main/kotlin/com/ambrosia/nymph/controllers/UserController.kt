//package com.ambrosia.nymph.controllers
//
//import com.ambrosia.nymph.exceptions.KeycloakException
//import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount
//import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken
//import org.keycloak.representations.AccessToken
//import org.springframework.web.bind.annotation.GetMapping
//import org.springframework.web.bind.annotation.PostMapping
//import org.springframework.web.bind.annotation.RequestMapping
//import org.springframework.web.bind.annotation.RestController
//
//
//@RestController
//@RequestMapping("users")
//class UserController(userService: UserService) {
//    val userService: UserService
//
//    init {
//        this.userService = userService
//    }
//
//    @GetMapping(value = ["/me"])
//    fun loadUserDetail(authentication: KeycloakAuthenticationToken): AccessToken {
//        val account = authentication.details as SimpleKeycloakAccount
//        return account.keycloakSecurityContext
//            .token
//    }
//
//    @PostMapping
//    @Throws(KeycloakException::class)
//    fun createUser() {
//        userService.createKeycloakUser(user)
//    }
//
//    private val user: KeycloakUser
//        private get() = KeycloakUser.builder()
//            .username("a")
//            .email("a")
//            .firstName("a")
//            .lastName("a")
//            .password("a")
//            .roles(listOf("user"))
//            .build()
//}