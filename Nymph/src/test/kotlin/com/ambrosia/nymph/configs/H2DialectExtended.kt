package com.ambrosia.nymph.configs

import org.hibernate.dialect.H2Dialect

class H2DialectExtended : H2Dialect() {
    override fun toBooleanValueString(bool: Boolean): String {
        return if (bool) "TRUE" else "FALSE"
    }
}
