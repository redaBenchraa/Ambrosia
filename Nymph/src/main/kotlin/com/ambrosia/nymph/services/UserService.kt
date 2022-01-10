package com.ambrosia.nymph.services

import com.ambrosia.nymph.configs.EnvironmentProperties
import com.ambrosia.nymph.exceptions.KeycloakException
import com.ambrosia.nymph.models.KeycloakUser
import org.apache.commons.collections4.CollectionUtils.isNotEmpty
import org.apache.commons.collections4.CollectionUtils.subtract
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.admin.client.resource.UsersResource
import org.keycloak.representations.idm.RoleRepresentation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import java.util.Objects.isNull
import java.util.stream.Collectors

@Service
class UserService {
	val logger: Logger = LoggerFactory.getLogger(UserService::class.java)

	@Autowired
	lateinit var keycloakService: KeycloakService

	@Autowired
	lateinit var environmentProperties: EnvironmentProperties

	fun createKeycloakUser(user: KeycloakUser) {
		val realmResource = realmResource
		val usersResource = getUsersResource(realmResource)
		val userRepresentation = keycloakService.getUserRepresentation(user)
		try {
			usersResource.create(userRepresentation).use { response ->
				if (response.status != 201) {
					throw KeycloakException("error.keycloak.create_user")
				}
			}
		} catch (e: Exception) {
			throw KeycloakException("error.keycloak.create_user")
		}
	}

	fun updateRoles(user: KeycloakUser) {
		val realmResource = realmResource
		val usersResource = getUsersResource(realmResource)
		val currentUser = usersResource.search(user.username)
			.stream()
			.findFirst()
			.orElseThrow { KeycloakException("error.keycloak.user_not_found") }
		val realmRoles = realmResource.roles()
			.list()
		val currentRoles = usersResource[currentUser.id]
			.roles()
			.all
			.realmMappings
			.stream()
			.map { obj: RoleRepresentation -> obj.name }
			.collect(Collectors.toList())
		val rolesToAdd: MutableList<RoleRepresentation>? =
			subtract(user.roles, currentRoles)
				.stream()
				.map { role ->
					realmRoles.stream()
						.filter { x: RoleRepresentation ->
							(x.name == role)
						}
						.findFirst()
						.orElse(null)
				}
				.collect(Collectors.toList())
		val rolesToRemove: MutableList<RoleRepresentation>? =
			subtract(currentRoles, user.roles)
				.stream()
				.map { role ->
					realmRoles.stream()
						.filter { x: RoleRepresentation ->
							(x.name == role)
						}
						.findFirst()
						.orElse(null)
				}
				.collect(Collectors.toList())
		try {
			if (isNotEmpty(rolesToRemove)) {
				usersResource[currentUser.id]
					.roles()
					.realmLevel()
					.remove(rolesToRemove)
			}
			if (isNotEmpty(rolesToAdd)) {
				usersResource[currentUser.id]
					.roles()
					.realmLevel()
					.add(rolesToAdd)
			}
		} catch (e: Exception) {
			logger.info(e.message)
			throw KeycloakException("error.keycloak.attribute_roles")
		}
	}

	fun deleteKeycloakUser(user: KeycloakUser) {
		val realmResource = realmResource
		val usersResource = getUsersResource(realmResource)
		val currentUser = usersResource.search(user.username)
			.stream()
			.findFirst()
			.orElse(null)
		if (Objects.nonNull(currentUser)) {
			usersResource.delete(currentUser.id)
		}
	}

	private val realmResource: RealmResource
		get() {
			val realmResource: RealmResource = keycloakService.realmManager
				.realm(environmentProperties.realm())
			if (isNull(realmResource)) {
				throw KeycloakException("error.keycloak.retrieve_realm")
			}
			return realmResource
		}

	private fun getUsersResource(realmResource: RealmResource): UsersResource {
		val usersResource = realmResource.users()
		if (isNull(usersResource)) {
			throw KeycloakException("error.keycloak.retrieve_user_resource")
		}
		return usersResource
	}
}