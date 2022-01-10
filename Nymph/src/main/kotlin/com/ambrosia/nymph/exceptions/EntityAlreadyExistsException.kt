package com.ambrosia.nymph.exceptions

class EntityAlreadyExistsException(
	entityClass: Class<*>, vararg searchParamsMap: Any
) : EntityException(entityClass, searchParamsMap)