package org.brailleblaster.libembosser.spi

sealed class EmbosserOption(val name: String) {
    class BooleanOption(name: String, var value: Boolean) : EmbosserOption(name)
}