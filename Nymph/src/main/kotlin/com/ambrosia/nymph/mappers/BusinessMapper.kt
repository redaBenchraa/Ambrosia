package com.ambrosia.nymph.mappers

import com.ambrosia.nymph.dtos.BusinessRegistrationDto
import com.ambrosia.nymph.entities.Business

fun Business.toDto(): BusinessRegistrationDto =
    BusinessRegistrationDto(
        id,
        name,
        phoneNumber,
        email,
        description,
        slogan,
        logo,
        location,
        currency
    )

fun BusinessRegistrationDto.toEntity(): Business =
    Business(id, name!!, phoneNumber!!, email!!, description, slogan, logo, location, currency)
