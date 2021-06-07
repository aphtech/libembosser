package org.brailleblaster.libembosser.spi

import java.util.*

interface OptionIdentifier {
    val id: String
    fun getDisplayName(locale: Locale): String
}

sealed class EmbosserOption {
    abstract val value: String
    abstract fun copy(value: String): EmbosserOption
    class BooleanOption(val boolean: Boolean) : EmbosserOption() {
        constructor(value: String) : this(value.toBoolean())
        override val value: String
        get() = boolean.toString()
        override fun copy(value: String) = BooleanOption(value)
        fun copy(value: Boolean) = BooleanOption(value)
    }
    class ByteArrayOption(vararg bytes: Byte) : EmbosserOption() {
        constructor(value: String) : this(*Base64.getDecoder().decode(value))
        val bytes: ByteArray = bytes
        get() = field.copyOf()
        override val value: String
        get() = Base64.getEncoder().encodeToString(bytes)
        override fun copy(value: String) = ByteArrayOption(value)
        fun copy(value: ByteArray) = ByteArrayOption(*value)
    }

    class StringOption(override val value: String) : EmbosserOption() {
        override fun copy(value: String) = StringOption(value)
    }
    class IntOption(val int: Int) : EmbosserOption() {
        constructor(value: String) : this(value.toInt())
        override val value: String
        get() = int.toString()
        override fun copy(value: String) = IntOption(value)
        fun copy(value: Int) = IntOption(value)
    }
}
