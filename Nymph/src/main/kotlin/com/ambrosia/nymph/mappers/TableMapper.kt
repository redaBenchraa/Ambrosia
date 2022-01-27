package com.ambrosia.nymph.mappers

import com.ambrosia.nymph.dtos.TableDto
import com.ambrosia.nymph.entities.Table

fun Table.toDto(): TableDto = TableDto(id, number, isAvailable)

fun TableDto.toEntity(): Table = Table(number!!, isAvailable!!).apply { id }
