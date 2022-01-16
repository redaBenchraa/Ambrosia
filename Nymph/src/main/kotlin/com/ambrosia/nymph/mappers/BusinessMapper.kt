package com.ambrosia.nymph.mappers

import com.ambrosia.nymph.dtos.BusinessRegistrationDto
import com.ambrosia.nymph.entities.Business


fun Business.toDto(): BusinessRegistrationDto = BusinessRegistrationDto(
	id = id,
	name = name,
	phoneNumber = phoneNumber,
	email = email,
	description = description,
	slogan = slogan,
	logo = logo,
	location = location,
	currency = currency,
)

fun BusinessRegistrationDto.toEntity(): Business = Business(
	id = id,
	name = name,
	phoneNumber = phoneNumber,
	email = email,
	description = description,
	slogan = slogan,
	logo = logo,
	location = location,
	currency = currency,
)