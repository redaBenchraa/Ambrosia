package com.ambrosia.nymph.mappers

import com.ambrosia.nymph.dtos.TableDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Table

fun Table.toDto(): TableDto = TableDto(id, number, isAvailable)

fun TableDto.toEntity(business: Business): Table = Table(number ?: 0, isAvailable ?: true, business).apply { id }
