package com.ambrosia.nymph.mappers

import com.ambrosia.nymph.dtos.SessionDto
import com.ambrosia.nymph.entities.Session

fun Session.toDto(): SessionDto = SessionDto(id, paid, closed, approved)
