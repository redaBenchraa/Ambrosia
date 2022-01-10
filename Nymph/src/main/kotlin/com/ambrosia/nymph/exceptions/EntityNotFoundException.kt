package com.ambrosia.nymph.exceptions


class EntityNotFoundException(
    entityClass: Class<*>,
    vararg searchParamsMap: Any
) :
    EntityException(entityClass, searchParamsMap) {
}