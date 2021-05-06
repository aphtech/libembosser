package org.brailleblaster.libembosser.spi

sealed class EmbosserOption {
    abstract val value: Any
    class BooleanOption(override val value: Boolean) : EmbosserOption()
    class ByteArrayOption(vararg value: Byte) : EmbosserOption() {
        override val value: ByteArray = value.copyOf()
        get() = field.copyOf()
    }

    class StringOption(override val value: String) : EmbosserOption()
    class IntOption(override val value: Int) : EmbosserOption()
}
