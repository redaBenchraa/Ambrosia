package com.ambrosia.nymph.mappers

import com.ambrosia.nymph.constants.Currency.EUR
import com.ambrosia.nymph.constants.UNDEFINED_VALUE
import com.ambrosia.nymph.dtos.BusinessRegistrationDto
import com.ambrosia.nymph.entities.Business

fun Business.toDto(): BusinessRegistrationDto =
    BusinessRegistrationDto(
        id, name, phoneNumber, email, description, slogan, logo, location, currency, available
    )

fun BusinessRegistrationDto.toEntity(): Business =
    Business(
        name ?: UNDEFINED_VALUE,
        phoneNumber ?: UNDEFINED_VALUE,
        email ?: UNDEFINED_VALUE,
        description,
        slogan,
        logo,
        location,
        currency ?: EUR.name,
        available ?: true
    ).apply { id }
