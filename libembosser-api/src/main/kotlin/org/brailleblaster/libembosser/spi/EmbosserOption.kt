package org.brailleblaster.libembosser.spi

import java.util.*

sealed class EmbosserOption {
    abstract val value: String
    class BooleanOption(val boolean: Boolean) : EmbosserOption() {
        constructor(value: String) : this(value.toBoolean())
        override val value: String
        get() = boolean.toString()
    }
    class ByteArrayOption(vararg bytes: Byte) : EmbosserOption() {
        constructor(value: String) : this(*Base64.getDecoder().decode(value))
        val bytes: ByteArray = bytes
        get() = field.copyOf()
        override val value: String
        get() = Base64.getEncoder().encodeToString(bytes)
    }

    class StringOption(override val value: String) : EmbosserOption()
    class IntOption(val int: Int) : EmbosserOption() {
        constructor(value: String) : this(value.toInt())
        override val value: String
        get() = int.toString()
    }
}
