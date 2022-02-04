package com.ambrosia.nymph.mappers

import com.ambrosia.nymph.dtos.TableDto
import com.ambrosia.nymph.entities.Business
import com.ambrosia.nymph.entities.Table

fun Table.toDto(): TableDto = TableDto(id, number, available)

fun TableDto.toEntity(business: Business): Table = Table(number ?: 0, available ?: true, business).apply { id }
