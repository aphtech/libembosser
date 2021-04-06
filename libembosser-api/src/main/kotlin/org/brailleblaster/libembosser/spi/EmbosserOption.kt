package org.brailleblaster.libembosser.spi

sealed class EmbosserOption {
    data class BooleanOption(val value: Boolean) : EmbosserOption()
    data class MultipleChoiceOption<T : MultipleChoiceValue>(val value: T, val choices: List<T>) : EmbosserOption() {
        init {
            require(choices.isNotEmpty())
            require(choices.contains(value))
        }
    }

    data class StringOption(val value: String) : EmbosserOption()

    interface MultipleChoiceValue {
        val displayString: String
    }
}
