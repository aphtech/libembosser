package org.brailleblaster.libembosser.spi

sealed class EmbosserOption(val name: String) {
    class BooleanOption(name: String, var value: Boolean) : EmbosserOption(name)
    class StringChoiceOption(name: String, value: String, val choices: List<String>) : EmbosserOption(name) {
        init {
            require(choices.isNotEmpty())
        }
        var value = value
        set(value) {
            require(choices.contains(value))
            field = value
        }
    }
    class StringOption(name: String, var value: String) : EmbosserOption(name)
}