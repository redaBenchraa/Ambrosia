package com.ambrosia.nymph.handlers

import com.ambrosia.nymph.constants.ENTITY_ALREADY_EXITS
import com.ambrosia.nymph.constants.ENTITY_NOT_FOUND
import com.ambrosia.nymph.constants.KEYCLOAK
import com.ambrosia.nymph.constants.SESSION_CLOSED
import com.ambrosia.nymph.constants.VIOLATIONS
import com.ambrosia.nymph.entities.Session
import com.ambrosia.nymph.exceptions.EntityAlreadyExistsException
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.exceptions.KeycloakException
import com.ambrosia.nymph.exceptions.SessionClosedException
import com.ambrosia.nymph.utils.Translator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.zalando.problem.Problem
import org.zalando.problem.Status
import org.zalando.problem.spring.web.advice.ProblemHandling
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait
import org.zalando.problem.violations.Violation
import java.net.URI

@ControllerAdvice
class RuntimeExceptionHandler : ProblemHandling, SecurityAdviceTrait {

    @Autowired
    lateinit var translator: Translator

    override fun defaultConstraintViolationType(): URI = URI.create(VIOLATIONS)

    override fun isCausalChainsEnabled(): Boolean = true

    override fun createViolation(error: FieldError): Violation =
        Violation(formatFieldName(error.field), translator.toLocale(error.defaultMessage))

    @ExceptionHandler(EntityNotFoundException::class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    fun handleEntityNotFoundException(ex: EntityNotFoundException): ResponseEntity<Problem> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                Problem.builder()
                    .withType(URI.create(ENTITY_NOT_FOUND))
                    .withTitle(translator.toLocale("error.entityNotFound"))
                    .withStatus(Status.NOT_FOUND)
                    .withDetail(
                        String.format(
                            translator.toLocale("error.entityNotFoundDetails"),
                            ex.entityClass.simpleName,
                            ex.parameters.toString()
                        )
                    )
                    .build()
            )
    }

    @ExceptionHandler(SessionClosedException::class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    fun handleEntityNotFoundException(ex: SessionClosedException): ResponseEntity<Problem> {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(
                Problem.builder()
                    .withType(URI.create(SESSION_CLOSED))
                    .withTitle(translator.toLocale("error.sessionIsClosed"))
                    .withStatus(Status.CONFLICT)
                    .withDetail(
                        String.format(
                            translator.toLocale("error.sessionIsClosedDetails"),
                            Session::class.java.simpleName,
                            ex.parameters.toString()
                        )
                    )
                    .build()
            )
    }


    @ExceptionHandler(EntityAlreadyExistsException::class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    fun handleEntityAlreadyExistsException(ex: EntityAlreadyExistsException): ResponseEntity<Problem> {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(
                Problem.builder()
                    .withType(URI.create(ENTITY_ALREADY_EXITS))
                    .withTitle(translator.toLocale("error.entityAlreadyExists"))
                    .withStatus(Status.CONFLICT)
                    .withDetail(
                        String.format(
                            translator.toLocale("error.entityAlreadyExistsDetails"),
                            ex.entityClass.simpleName,
                            ex.parameters
                        )
                    )
                    .build()
            )
    }

    @ExceptionHandler(KeycloakException::class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleKeycloakException(ex: KeycloakException): ResponseEntity<Problem> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                Problem.builder()
                    .withType(URI.create(KEYCLOAK))
                    .withTitle(translator.toLocale(ex.message))
                    .withStatus(Status.INTERNAL_SERVER_ERROR)
                    .build()
            )
    }
}
