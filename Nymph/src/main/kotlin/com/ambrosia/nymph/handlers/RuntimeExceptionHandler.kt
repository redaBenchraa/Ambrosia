package com.ambrosia.nymph.handlers

import com.ambrosia.nymph.constants.Urls
import com.ambrosia.nymph.exceptions.EntityAlreadyExistsException
import com.ambrosia.nymph.exceptions.EntityNotFoundException
import com.ambrosia.nymph.exceptions.KeycloakException
import com.ambrosia.nymph.utils.Translator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
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


	override fun defaultConstraintViolationType(): URI = URI.create(Urls.VIOLATIONS)

	override fun isCausalChainsEnabled(): Boolean = true

	override fun createViolation(error: FieldError): Violation =
		Violation(formatFieldName(error.field), translator.toLocale(error.defaultMessage))

	override fun createViolation(error: ObjectError): Violation =
		Violation(formatFieldName(error.objectName), translator.toLocale(error.defaultMessage))

	@ExceptionHandler(EntityNotFoundException::class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	fun handleEntityNotFoundException(ex: EntityNotFoundException): ResponseEntity<Problem> {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(
				Problem.builder()
					.withType(URI.create(Urls.NOT_FOUND))
					.withTitle(translator.toLocale("error.entityNotFound"))
					.withStatus(Status.NOT_FOUND)
					.withDetail(
						String.format(
							translator.toLocale("error.entityNotFoundDetails"),
							ex.entityClass.simpleName, ex.parameters.toString()
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
					.withType(URI.create(Urls.ALREADY_EXITS))
					.withTitle(translator.toLocale("error.entityAlreadyExists"))
					.withStatus(Status.CONFLICT)
					.withDetail(
						String.format(
							translator.toLocale("error.entityAlreadyExistsDetails"),
							ex.entityClass.simpleName, ex.parameters
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
					.withType(URI.create(Urls.KEYCLOAK))
					.withTitle(translator.toLocale(ex.message))
					.withStatus(Status.INTERNAL_SERVER_ERROR)
					.build()
			)
	}
}